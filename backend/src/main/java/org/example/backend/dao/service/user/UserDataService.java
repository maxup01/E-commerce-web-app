package org.example.backend.dao.service.user;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.exception.privilege.PrivilegeNotUpdatedException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.example.backend.exception.role.RoleNotSavedException;
import org.example.backend.exception.role.RoleNotUpdatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserDataService {

    private final Pattern privilegeNamePattern;
    private final Pattern roleNamePattern;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public Privilege saveNewPrivilege(String privilegeName) {

        if((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException(
                    "Privilege needs to match privilege name pattern and cannot be null");
        else if (privilegeRepository.findByName(privilegeName) != null)
            throw new PrivilegeNotFoundException(
                    "Privilege name cannot be the same as one of the existed privileges");

        Privilege newPrivilege = new Privilege(privilegeName);
        Privilege privilegeSaved;

        try{
            privilegeSaved = privilegeRepository.save(newPrivilege);
        } catch (Exception e){
            throw new PrivilegeNotSavedException(e.getMessage());
        }

        return privilegeSaved;
    }

    @Transactional
    public Privilege updateNameOfPrivilegeById(Long id, String privilegeName) {

        Privilege privilegeSaved;

        if((id == null) || (id <= 0))
            throw new BadArgumentException("Incorrect argument: id");
        else if ((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException("Incorrect argument: privilegeName");

        Privilege privilege = privilegeRepository.findById(id).orElse(null);

        if(privilege == null){
            throw new PrivilegeNotFoundException("Privilege with id " + id + " not found");
        }

        privilege.setName(privilegeName);

        try{
            privilegeSaved = privilegeRepository.save(privilege);
        } catch (Exception e){
            throw new PrivilegeNotUpdatedException(e.getMessage());
        }

        return privilegeSaved;
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

        if(roleToDelete.getPrivileges() != null){
            roleToDelete.getPrivileges().forEach(privilege -> {
                List<Privilege> privileges = roleToDelete.getPrivileges();
                privileges.removeIf(pomPrivilege -> pomPrivilege.getId() == idOfRoleToDelete);
            });
        }

        if(roleToAssign.getUsers() == null)
            roleToAssign.setUsers(new ArrayList<>());

        if (roleToDelete.getUsers() != null) {
            roleToDelete.getUsers().forEach(user -> {
                user.setRole(roleToAssign);
                roleToAssign.getUsers().add(user);
            });
        }

        roleRepository.delete(roleToDelete);
    }

    @Transactional
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Autowired
    public UserDataService(PrivilegeRepository privilegeRepository, RoleRepository roleRepository) {
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        privilegeNamePattern = Pattern.compile("[A-Z]+_PRIVILEGE");
        roleNamePattern = Pattern.compile("ROLE_[A-Z]+");
    }
}
