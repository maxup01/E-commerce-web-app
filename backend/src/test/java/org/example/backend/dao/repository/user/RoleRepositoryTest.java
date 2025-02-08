package org.example.backend.dao.repository.user;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RoleRepositoryTest {

    final private String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    final private String RANDOM_ROLE_NAME = "ROLE_NAME";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testOfSave(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role1 = new Role(RANDOM_ROLE_NAME, List.of(privilege));

        assertDoesNotThrow(() -> {
            roleRepository.save(role1);
            entityManager.flush();
        });

        //Role is incorrectly created because has the name us the role above
        Role incorrectlyCreatedRole = new Role(RANDOM_ROLE_NAME, List.of(privilege));

        assertThrows(DataIntegrityViolationException.class, () -> {
            roleRepository.save(incorrectlyCreatedRole);
            entityManager.flush();
        } );
    }

    @Test
    public void testOfFindByName(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        Role foundRole = roleRepository.findByName(RANDOM_ROLE_NAME);

        assertNotNull(foundRole.getId());
        assertEquals(foundRole.getName(), RANDOM_ROLE_NAME);
        assertEquals(foundRole.getPrivileges().size(), 1);
    }
}
