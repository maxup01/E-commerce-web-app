package org.example.backend.dao.service.user;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.exception.privilege.PrivilegeNotUpdatedException;
import org.example.backend.exception.role.RoleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserDataService {

    private final Pattern privilegeNamePattern;
    private final Pattern roleNamePattern;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public Privilege saveNewPrivilege(Privilege privilege) {

        Privilege privilegeSaved;

        if (privilege == null)
            throw new BadArgumentException("Privilege cannot be null");
        else if((privilege.getId() != null))
            throw new BadArgumentException("Privilege id cannot be not null");
        else if((privilege.getName() == null) || (!privilegeNamePattern.matcher(privilege.getName()).matches()))
            throw new BadArgumentException(
                    "Privilege needs to match privilege name pattern and cannot be null");
        else if (privilegeRepository.findByName(privilege.getName()) != null)
            throw new PrivilegeNotSavedException(
                    "Privilege name cannot be the same as one of the existed privileges");

        try{
            privilegeSaved = privilegeRepository.save(privilege);
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

        privilegeRepository.deleteById(id);
    }

    @Transactional
    public List<Privilege> getAllPrivileges() {
        return privilegeRepository.findAll();
    }

    @Transactional
    public Role getRoleById(Long id){

        Role foundRole;

        if((id == null) || (id <= 0)){
            throw new BadArgumentException("Incorrect argument: id");
        }

        foundRole = roleRepository.findById(id).orElseThrow(() -> {
            return new RoleNotFoundException("Role with id " + id + " not found");
        });

        return foundRole;
    }

    @Transactional
    public Role getRoleByName(String name) {

        Role foundRole;

        if((name == null) || (!roleNamePattern.matcher(name).matches())){
            throw new BadArgumentException("Incorrect argument: name");
        }

        foundRole = roleRepository.findByName(name);

        if(foundRole == null){
            throw new RoleNotFoundException("Role with name " + name + " not found");
        }

        return foundRole;
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
