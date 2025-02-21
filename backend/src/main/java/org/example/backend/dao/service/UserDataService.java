package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.image.UserImage;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.image.UserImageRepository;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.example.backend.exception.role.RoleNotSavedException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.exception.user.UserNotSavedException;
import org.example.backend.model.PrivilegeModel;
import org.example.backend.model.RoleModel;
import org.example.backend.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserDataService {

    private final Pattern privilegeNamePattern;
    private final Pattern roleNamePattern;
    private final Pattern userEmailPattern;
    private final Pattern userPasswordPattern;

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserDataService(PrivilegeRepository privilegeRepository, RoleRepository roleRepository,
                           UserImageRepository userImageRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.privilegeNamePattern = Pattern.compile("[A-Z]+_PRIVILEGE");
        this.roleNamePattern = Pattern.compile("ROLE_[A-Z]+");
        this.userEmailPattern = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-z]+");
        this.userPasswordPattern = Pattern.compile("[A-Z]+[a-zA-Z]\\w+");
    }

    @Transactional
    public PrivilegeModel saveNewPrivilege(String privilegeName) {

        if((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException(
                    "Privilege needs to match privilege name pattern and cannot be null");
        else if (privilegeRepository.findByName(privilegeName) != null)
            throw new PrivilegeNotSavedException("Privilege with name " + privilegeName + " already exists");

        Privilege newPrivilege = new Privilege(privilegeName);
        newPrivilege = privilegeRepository.save(newPrivilege);

        return PrivilegeModel.fromPrivilege(newPrivilege);
    }

    @Transactional
    public PrivilegeModel updateNameOfPrivilegeById(Long id, String privilegeName) {

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if ((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException("Incorrect argument: privilegeName");

        Privilege privilege = privilegeRepository.findById(id).orElse(null);

        if(privilege == null){
            throw new PrivilegeNotFoundException("Privilege with id " + id + " not found");
        }

        privilege.setName(privilegeName);
        privilege = privilegeRepository.save(privilege);

        return PrivilegeModel.fromPrivilege(privilege);
    }

    @Transactional
    public PrivilegeModel getPrivilegeById(Long id) {

        if ((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");

        Privilege foundPrivilege = privilegeRepository.findById(id).orElseThrow(() ->
                new PrivilegeNotFoundException("Privilege with id " + id + " not found"));

        return PrivilegeModel.fromPrivilege(foundPrivilege);
    }

    @Transactional
    public PrivilegeModel getPrivilegeByName(String name) {

        if((name == null) || (!privilegeNamePattern.matcher(name).matches())){
            throw new BadArgumentException("Incorrect argument: name");
        }

        Privilege foundPrivilege = privilegeRepository.findByName(name);

        if (foundPrivilege == null) {
            throw new PrivilegeNotFoundException("Privilege with name " + name + " not found");
        }

        return PrivilegeModel.fromPrivilege(foundPrivilege);
    }

    @Transactional
    public void deletePrivilegeById(Long id) {

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");

        Privilege privilege = privilegeRepository.findById(id).orElseThrow(() -> {
            return new PrivilegeNotFoundException("Privilege with id " + id + " not found");
        });

        if(privilege.getRoles() != null){
            privilege.getRoles().forEach(role -> {
                List<Privilege> privileges = role.getPrivileges();
                privileges.removeIf((pomPrivilege) -> pomPrivilege.getId() == id);
                role.setPrivileges(privileges);
            });
        }

        privilegeRepository.delete(privilege);
    }

    @Transactional
    public List<PrivilegeModel> getAllPrivileges() {

        List<Privilege> privileges = privilegeRepository.findAll();

        ArrayList<PrivilegeModel> privilegeModels = new ArrayList<>();

        privileges.forEach(privilege -> {
            privilegeModels.add(PrivilegeModel.fromPrivilege(privilege));
        });

        return privilegeModels;
    }

    @Transactional
    public RoleModel saveNewRole(RoleModel role) {

        if((role == null) || (role.getName() == null) || (!roleNamePattern.matcher(role.getName()).matches()))
            throw new BadArgumentException("Incorrect argument field: Role.name");
        else if(roleRepository.findByName(role.getName()) != null)
            throw new RoleNotSavedException("Role with name " + role.getName() + " already exists");
        else if((role.getPrivileges() == null) || (role.getPrivileges().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: Role.privileges");

        List<Privilege> privileges = new ArrayList<>();

        role.getPrivileges().forEach(privilege -> {

            if((privilege.getName() == null) || (!privilegeNamePattern.matcher(privilege.getName()).matches()))
                throw new BadArgumentException("Incorrect argument field: Role.privileges.name");

            Privilege foundPrivilege = privilegeRepository.findByName(privilege.getName());

            if(foundPrivilege == null){
                throw new PrivilegeNotFoundException("Privilege with name " + privilege.getName() + " not found");
            }

            privileges.add(foundPrivilege);
        });

        Role roleEntity = new Role(role.getName(), privileges);
        roleEntity = roleRepository.save(roleEntity);

        Role finalRole = roleEntity;
        privileges.forEach(privilege -> {
            privilege.getRoles().add(finalRole);
        });

        return RoleModel.fromRole(roleEntity);
    }

    @Transactional
    public RoleModel updateRoleNameById(Long id, String newName) {

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if ((newName == null) || (!roleNamePattern.matcher(newName).matches()))
            throw new BadArgumentException("Incorrect argument: newName");

        Role foundRole = roleRepository.findById(id).orElse(null);

        if(foundRole == null)
            throw new RoleNotFoundException("Role with id " + id + " not found");

        foundRole.setName(newName);
        roleRepository.save(foundRole);

        return RoleModel.fromRole(foundRole);
    }

    @Transactional
    public RoleModel deleteRolePrivilegeByIdAndName(Long id, String privilegeName) {

        if ((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if ((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException("Incorrect argument: privilegeName");

        Role role = roleRepository.findById(id).orElse(null);

        if (role == null)
            throw new RoleNotFoundException("Role with id " + id + " not found");

        Privilege privilege = privilegeRepository.findByName(privilegeName);

        if (privilege == null)
            throw new PrivilegeNotFoundException("Privilege with name " + privilegeName + " not found");

        role.getPrivileges().removeIf(pomPrivilege -> pomPrivilege.getId().equals(id));
        roleRepository.save(role);

        return RoleModel.fromRole(role);
    }

    @Transactional
    public RoleModel addPrivilegeToRoleByIdAndName(Long id, String privilegeName) {

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException("Incorrect argument: privilegeName");

        Role role = roleRepository.findById(id).orElseThrow(() -> {
            return new RoleNotFoundException("Role with id " + id + " not found");
        });

        Privilege privilege = privilegeRepository.findByName(privilegeName);

        if(privilege == null)
            throw new PrivilegeNotFoundException("Privilege with name " + privilegeName + " not found");

        role.getPrivileges().add(privilege);
        roleRepository.save(role);

        return RoleModel.fromRole(role);
    }

    @Transactional
    public RoleModel getRoleById(Long id){

        if((id == null) || (id <= 0)){
            throw new BadArgumentException("Incorrect argument: id");
        }

        Role foundRole = roleRepository.findById(id).orElseThrow(() -> {
            return new RoleNotFoundException("Role with id " + id + " not found");
        });

        return RoleModel.fromRole(foundRole);
    }

    @Transactional
    public RoleModel getRoleByName(String name) {

        if((name == null) || (!roleNamePattern.matcher(name).matches())){
            throw new BadArgumentException("Incorrect argument: name");
        }

        Role foundRole = roleRepository.findByName(name);

        if(foundRole == null){
            throw new RoleNotFoundException("Role with name " + name + " not found");
        }

        return RoleModel.fromRole(foundRole);
    }

    @Transactional
    public List<RoleModel> getAllRoles() {

        List<Role> roles = roleRepository.findAll();

        ArrayList<RoleModel> roleModelsList = new ArrayList<>();

        roles.forEach(role -> {
            roleModelsList.add(RoleModel.fromRole(role));
        });

        return roleModelsList;
    }

    @Transactional
    public void deleteRoleById(Long idOfRoleToDelete, Long idOfRoleToAssignToUsers) {

        if((idOfRoleToDelete == null) || (idOfRoleToDelete <= 0))
            throw new BadArgumentException("Incorrect argument: idOfRoleToDelete");

        if((idOfRoleToAssignToUsers == null) || (idOfRoleToAssignToUsers <= 0))
            throw new BadArgumentException("Incorrect argument: idOfRoleToAssignToUsers");

        Role roleToDelete = roleRepository.findById(idOfRoleToDelete).orElse(null);
        Role roleToAssign = roleRepository.findById(idOfRoleToAssignToUsers).orElse(null);

        if(roleToDelete == null)
            throw new RoleNotFoundException("Role to delete with id " + idOfRoleToDelete + " not found");

        if(roleToAssign == null)
            throw new RoleNotFoundException("Role to assign to users with id " + idOfRoleToAssignToUsers + " not found");

        if (roleToDelete.getUsers() != null) {
            roleToDelete.getUsers().forEach(user -> {
                user.setRole(roleToAssign);
            });
        }

        roleRepository.delete(roleToDelete);
    }

    @Transactional
    public UserModel saveNewUser(UserModel userModel, String roleName) {

        if(userModel == null)
            throw new BadArgumentException("Null argument: userModel");
        else if((userModel.getFirstName() == null) || (userModel.getFirstName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: userModel.firstName");
        else if((userModel.getLastName() == null) || (userModel.getLastName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: userModel.lastName");
        else if((userModel.getEmail() == null) || (!userEmailPattern.matcher(userModel.getEmail()).matches()))
            throw new BadArgumentException("Incorrect argument field: userModel.email");
        else if((userModel.getPassword() == null) || (!userPasswordPattern.matcher(userModel.getPassword()).matches()))
            throw new BadArgumentException("Incorrect argument field: userModel.password");
        else if((userModel.getBirthDate() == null) || (userModel.getBirthDate().isAfter(LocalDate.now())))
            throw new BadArgumentException("Incorrect argument field: userModel.birthDate");
        else if((roleName == null) || (!roleNamePattern.matcher(roleName).matches()))
            throw new BadArgumentException("Incorrect argument: roleName");

        User foundUser = userRepository.findByEmail(userModel.getEmail());

        if(foundUser != null)
            throw new UserNotSavedException("User with email " + userModel.getEmail() + " already exists");

        Role foundRole = roleRepository.findByName(roleName);

        if(foundRole == null)
            throw new RoleNotFoundException("Role with name " + roleName + " not found");

        User newUser = new User(userModel.getFirstName(), userModel.getLastName(), userModel.getEmail(),
                bCryptPasswordEncoder.encode(userModel.getPassword()), userModel.getBirthDate(), foundRole);
        newUser = userRepository.save(newUser);

        return UserModel.fromUser(newUser);
    }

    @Transactional
    public UserModel updateUserFirstNameById(UUID id, String firstName) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((firstName == null) || (firstName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: firstName");

        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            return new UserNotFoundException("User with id " + id + " not found");
        });

        foundUser.setFirstName(firstName);
        userRepository.save(foundUser);

        return UserModel.fromUser(foundUser);
    }

    @Transactional
    public UserModel updateUserLastNameById(UUID id, String lastName) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((lastName == null) || (lastName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: lastName");

        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            return new UserNotFoundException("User with id " + id + " not found");
        });

        foundUser.setLastName(lastName);
        userRepository.save(foundUser);

        return UserModel.fromUser(foundUser);
    }

    @Transactional
    public UserModel updateUserPasswordById(UUID id, String password) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((password == null) || (!userPasswordPattern.matcher(password).matches()))
            throw new BadArgumentException("Incorrect argument: password");

        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            return new UserNotFoundException("User with id " + id + " not found");
        });

        foundUser.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(foundUser);

        return UserModel.fromUser(foundUser);
    }

    @Transactional
    public UserModel updateUserImageById(UUID id, byte[] image) {

         if(id == null)
             throw new BadArgumentException("Null argument: id");
         else if(image == null)
             throw new BadArgumentException("Incorrect argument: image");

         User foundUser = userRepository.findById(id).orElseThrow(() -> {
             return new UserNotFoundException("User with id " + id + " not found");
         });

         if(foundUser.getProfileImage() != null)
             userImageRepository.delete(foundUser.getProfileImage());

         UserImage userImage = userImageRepository.save(new UserImage(image));

         foundUser.setProfileImage(userImage);
         userRepository.save(foundUser);

         return UserModel.fromUser(foundUser);
    }

    @Transactional
    public UserModel getUserById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            return new UserNotFoundException("User with id " + id + " not found");
        });

        return UserModel.fromUser(foundUser);
    }

    @Transactional
    public UserModel getUserByEmail(String email){

        if((email == null) || (!userEmailPattern.matcher(email).matches()))
            throw new BadArgumentException("Incorrect argument: email");

        User foundUser = userRepository.findByEmail(email);

        if(foundUser == null)
            throw new UserNotFoundException("User with email " + email + " not found");

        return UserModel.fromUser(foundUser);
    }

    @Transactional
    public void deleteUserById(UUID id) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        User foundUser = userRepository.findById(id).orElse(null);

        if(foundUser == null)
            throw new UserNotFoundException("User with id " + id + " not found");

        userRepository.delete(foundUser);
    }
}
