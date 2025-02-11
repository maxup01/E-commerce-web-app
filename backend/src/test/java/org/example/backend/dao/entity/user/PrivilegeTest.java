package org.example.backend.dao.entity.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrivilegeTest {

    private final String RANDOM_PRIVILEGE_NAME = "WritePrivilege";

    @Test
    public void testOfPrivilegeConstructorWithNameArgument() {

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);

        assertNull(privilege.getId());
        assertEquals(privilege.getName(), RANDOM_PRIVILEGE_NAME);
        assertNotNull(privilege.getRoles());
    }
}
