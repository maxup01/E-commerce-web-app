package org.example.backend.dao.repository.user;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.user.Privilege;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PrivilegeRepositoryTest {

    final private String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Test
    public void testOfSave(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);

        assertDoesNotThrow(() -> {
            privilegeRepository.save(privilege);
            entityManager.flush();
        });

        Privilege incorrectlyCreatedPrivilege = new Privilege(RANDOM_PRIVILEGE_NAME);

        assertThrows(DataIntegrityViolationException.class, () -> {
            privilegeRepository.save(incorrectlyCreatedPrivilege);
            entityManager.flush();
        });
    }

    @Test
    public void testOfFindByName(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Privilege found = privilegeRepository.findByName(RANDOM_PRIVILEGE_NAME);

        assertNotNull(found.getId());
        assertEquals(found.getName(), RANDOM_PRIVILEGE_NAME);
    }
}
