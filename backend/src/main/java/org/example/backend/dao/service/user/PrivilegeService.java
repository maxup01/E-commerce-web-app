package org.example.backend.dao.service.user;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.privilege.PrivilegeNotFoundException;
import org.example.backend.exception.privilege.PrivilegeNotSavedException;
import org.example.backend.exception.privilege.PrivilegeNotUpdatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PrivilegeService {

    private final Pattern privilegeNamePattern;
    private final PrivilegeRepository privilegeRepository;

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
            throw new BadArgumentException("Argument id is incorrect");
        else if ((privilegeName == null) || (!privilegeNamePattern.matcher(privilegeName).matches()))
            throw new BadArgumentException("Argument privilegeName is incorrect");

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
            throw new BadArgumentException("Argument id is incorrect");

        return privilegeRepository.findById(id).orElseThrow(() ->
                new PrivilegeNotFoundException("Privilege with id " + id + " not found"));
    }

    @Transactional
    public Privilege getPrivilegeByName(String name) {

        if((name == null) || (!privilegeNamePattern.matcher(name).matches())){
            throw new BadArgumentException("Argument name is incorrect");
        }

        Privilege foundPrivilege = privilegeRepository.findByName(name);

        if (foundPrivilege == null) {
            throw new PrivilegeNotFoundException("Privilege with name " + name + " not found");
        }

        return foundPrivilege;
    }

    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
        privilegeNamePattern = Pattern.compile("[A-Z]+_PRIVILEGE");
    }
}
