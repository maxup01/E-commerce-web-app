package org.example.backend.dao.entity.user;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserTest {

    private final String RANDOM_FIRST_NAME = "firstName";
    private final String RANDOM_LAST_NAME = "lastName";
    private final String RANDOM_EMAIL = "email";
    private final String RANDOM_PASSWORD = "password";
    private final Date RANDOM_DATE_OF_BIRTH = new Date();
    private final Role RANDOM_ROLE = new Role();


    @Test
    public void testOfConstructorWithAllArgumentsWithoutIdArgument() {

        User user = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD,
                RANDOM_DATE_OF_BIRTH, RANDOM_ROLE);

        assertNull(user.getId());
        assertEquals(user.getFirstName(), RANDOM_FIRST_NAME);
        assertEquals(user.getLastName(), RANDOM_LAST_NAME);
        assertEquals(user.getEmail(), RANDOM_EMAIL);
        assertEquals(user.getPassword(), RANDOM_PASSWORD);
        assertEquals(user.getBirthDate(), RANDOM_DATE_OF_BIRTH);
        assertEquals(user.getRole(), RANDOM_ROLE);
    }
}
