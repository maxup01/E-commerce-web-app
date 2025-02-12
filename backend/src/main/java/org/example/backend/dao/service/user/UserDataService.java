package org.example.backend.dao.service.user;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.example.backend.exception.role.RoleNotSavedException;
import org.example.backend.exception.role.RoleNotUpdatedException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.exception.user.UserNotSavedException;
import org.example.backend.model.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

//TODO change return types to data models in future
@Service
public class UserDataService {

    private final Pattern privilegeNamePattern;
    private final Pattern roleNamePattern;
    private final Pattern userEmailPattern;
    private final Pattern userPasswordPattern;

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserDataService(PrivilegeRepository privilegeRepository, RoleRepository roleRepository,
                           UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.privilegeNamePattern = Pattern.compile("[A-Z]+_PRIVILEGE");
        this.roleNamePattern = Pattern.compile("ROLE_[A-Z]+");
        this.userEmailPattern = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-z]+");
        this.userPasswordPattern = Pattern.compile("[A-Z]+[a-zA-Z]\\w+");
    }

    @Transactional
    public Privilege saveNewPrivilege(String privilegeName) {

        if((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException(
                    "Privilege needs to match privilege name pattern and cannot be null");
        else if (privilegeRepository.findByName(privilegeName) != null)
            throw new PrivilegeNotFoundException(
                    "Privilege name cannot be the same as one of the existed privileges");

        Privilege newPrivilege = new Privilege(privilegeName);

        return privilegeRepository.save(newPrivilege);
    }

    @Transactional
    public Privilege updateNameOfPrivilegeById(Long id, String privilegeName) {

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if ((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException("Incorrect argument: privilegeName");

        Privilege privilege = privilegeRepository.findById(id).orElse(null);

        if(privilege == null){
            throw new PrivilegeNotFoundException("Privilege with id " + id + " not found");
        }

        privilege.setName(privilegeName);

        return privilegeRepository.save(privilege);
    }

    @Transactional
    public Privilege getPrivilegeById(Long id) {

        if ((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");

        return privilegeRepository.findById(id).orElseThrow(() ->
                new PrivilegeNotFoundException("Privilege with id " + id + " not found"));
    }

    @Transactional
    public Privilege getPrivilegeByName(String name) {

        if((name == null) || (!privilegeNamePattern.matcher(name).matches())){
            throw new BadArgumentException("Incorrect argument: name");
        }

        Privilege foundPrivilege = privilegeRepository.findByName(name);

        if (foundPrivilege == null) {
            throw new PrivilegeNotFoundException("Privilege with name " + name + " not found");
        }

        return foundPrivilege;
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
    public List<Privilege> getAllPrivileges() {
        return privilegeRepository.findAll();
    }

    @Transactional
    public Role saveNewRole(String roleName, List<String> privilegeNameList) {

        if((roleName == null) || (!roleNamePattern.matcher(roleName).matches()))
            throw new BadArgumentException("Incorrect argument: roleName");
        else if(roleRepository.findByName(roleName) != null)
            throw new RoleNotSavedException("Role with name " + roleName + " already exists");
        else if((privilegeNameList == null) || (privilegeNameList.isEmpty()))
            throw new BadArgumentException("Incorrect argument: privilegeList");

        List<Privilege> privileges = new ArrayList<>();

        privilegeNameList.forEach(privilege -> {

            if((!privilegeNamePattern.matcher(privilege).matches()))
                throw new BadArgumentException("Incorrect argument: privilegeNameList item");

            Privilege foundPrivilege = privilegeRepository.findByName(privilege);

            if(foundPrivilege == null){
                throw new PrivilegeNotFoundException("Privilege with name " + privilege + " not found");
            }

            privileges.add(foundPrivilege);
        });

        Role role = new Role(roleName, privileges);
        Role roleSaved;

        try{
            roleSaved = roleRepository.save(role);
        } catch (Exception e){
            throw new RoleNotSavedException(e.getMessage());
        }

        privileges.forEach(privilege -> {
            privilege.getRoles().add(roleSaved);
        });

        return roleSaved;
    }

    @Transactional
    public Role updateRoleNameById(Long id, String newRoleName) {

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if ((newRoleName == null) || (!roleNamePattern.matcher(newRoleName).matches()))
            throw new BadArgumentException("Incorrect argument: newRoleName");

        Role role = roleRepository.findById(id).orElse(null);

        if(role == null)
            throw new RoleNotFoundException("Role with id " + id + " not found");

        role.setName(newRoleName);
        Role updatedRole;

        try{
            updatedRole = roleRepository.save(role);
        } catch (Exception e){
            throw new RoleNotUpdatedException(e.getMessage());
        }

        return updatedRole;
    }

    @Transactional
    public Role deleteRoleRelationWithPrivilegeById(Long id, String privilegeName) {

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

        return role;
    }

    @Transactional
    public Role getRoleById(Long id){

        if((id == null) || (id <= 0)){
            throw new BadArgumentException("Incorrect argument: id");
        }

        Role foundRole = roleRepository.findById(id).orElseThrow(() -> {
            return new RoleNotFoundException("Role with id " + id + " not found");
        });

        return foundRole;
    }

    @Transactional
    public Role getRoleByName(String name) {

        if((name == null) || (!roleNamePattern.matcher(name).matches())){
            throw new BadArgumentException("Incorrect argument: name");
        }

        Role foundRole = roleRepository.findByName(name);

        if(foundRole == null){
            throw new RoleNotFoundException("Role with name " + name + " not found");
        }

        return foundRole;
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
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public User saveNewUser(UserModel userModel, String roleName) {

        if(userModel == null)
            throw new BadArgumentException("Null argument: userModel");
        else if((userModel.getFirstName() == null) || (userModel.getFirstName().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: userModel.firstName");
        else if((userModel.getLastName() == null) || (userModel.getLastName().isEmpty()))
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

        return userRepository.save(newUser);
    }

    @Transactional
    public User updateUserFirstNameByEmail(String email, String firstName) {

        if((email == null) || (!userEmailPattern.matcher(email).matches()))
            throw new BadArgumentException("Incorrect argument: email");
        else if((firstName == null) || (firstName.isEmpty()))
            throw new BadArgumentException("Incorrect argument: firstName");

        User foundUser = userRepository.findByEmail(email);

        if(foundUser == null)
            throw new UserNotFoundException("User with email " + email + " not found");

        foundUser.setFirstName(firstName);

        return userRepository.save(foundUser);
    }

    @Transactional
    public User updateUserLastNameByEmail(String email, String lastName) {

        if((email == null) || (!userEmailPattern.matcher(email).matches()))
            throw new BadArgumentException("Incorrect argument: email");
        else if((lastName == null) || (lastName.isEmpty()))
            throw new BadArgumentException("Incorrect argument: lastName");

        User foundUser = userRepository.findByEmail(email);

        if(foundUser == null)
            throw new UserNotFoundException("User with email " + email + " not found");

        foundUser.setLastName(lastName);

        return userRepository.save(foundUser);
    }

    @Transactional
    public User updateUserPasswordByEmail(String email, String password) {

        if((email == null) || (!userEmailPattern.matcher(email).matches()))
            throw new BadArgumentException("Incorrect argument: email");
        else if((password == null) || (!userPasswordPattern.matcher(password).matches()))
            throw new BadArgumentException("Incorrect argument: password");

        User foundUser = userRepository.findByEmail(email);

        if(foundUser == null)
            throw new UserNotFoundException("User with email " + email + " not found");

        foundUser.setPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(foundUser);
    }

    @Transactional
    public User getUserById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return userRepository.findById(id).orElseThrow(() -> {
            return new UserNotFoundException("User with id " + id + " not found");
        });
    }

    @Transactional
    public User getUserByEmail(String email){

        if((email == null) || (!userEmailPattern.matcher(email).matches()))
            throw new BadArgumentException("Incorrect argument: email");

        User foundUser = userRepository.findByEmail(email);

        if(foundUser == null)
            throw new UserNotFoundException("User with email " + email + " not found");

        return foundUser;
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
