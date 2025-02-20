package org.example.backend.config;

import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public SetupDataLoader(PrivilegeRepository privilegeRepository, RoleRepository roleRepository,
                           UserRepository userRepository) {
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

    private Privilege createPrivilegeIfNotExists(String privilegeName) {

        Privilege privilege = privilegeRepository.findByName(privilegeName);

        if(privilege != null)
            return privilege;

        privilege = new Privilege(privilegeName);

        return privilegeRepository.save(privilege);
    }

    private Role createRoleIfNotExists(String roleName, List<Privilege> privileges) {

        Role role = roleRepository.findByName(roleName);

        if(role != null)
            return role;

        role = new Role(roleName, privileges);

        return roleRepository.save(role);
    }

    private User createUserIfNotExists(String firstName, String lastName, String email, String password, LocalDate birthday
            , Role role) {

        if(userRepository.findByEmail(email) != null)
            return userRepository.findByEmail(email);

        User user = new User(firstName, lastName, email, password, birthday, role);

        return userRepository.save(user);
    }
}
