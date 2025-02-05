package org.example.backend.dao.entity.user;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {

    private final String RANDOM_ROLE_NAME = "ROLE_EXAMPLE";
    private final List<Privilege> RANDOM_PRIVILEGE_LIST = List.of(new Privilege(), new Privilege());

    @Test
    public void testOfConstructorWithNameAndPrivilegesArguments(){

        Role role = new Role(RANDOM_ROLE_NAME, RANDOM_PRIVILEGE_LIST);

        assertNull(role.getId());
        assertEquals(role.getName(), RANDOM_ROLE_NAME);
        assertEquals(role.getPrivileges(), RANDOM_PRIVILEGE_LIST);
    }
}
