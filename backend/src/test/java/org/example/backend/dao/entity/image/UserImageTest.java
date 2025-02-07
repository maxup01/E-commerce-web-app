package org.example.backend.dao.entity.image;


import org.example.backend.dao.entity.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserImageTest {

    private final byte[] RANDOM_IMAGE = new byte[10];
    private final User RANDOM_USER = new User();

    @Test
    public void testOfConstructorWithImageArgument() {

        UserImage userImage = new UserImage(RANDOM_IMAGE);

        assertNull(userImage.getId());
        assertEquals(userImage.getImage(), RANDOM_IMAGE);
        assertNull(userImage.getUser());
    }

    @Test
    public void testOfConstructorWithImageAndUserArguments() {

        UserImage userImage = new UserImage(RANDOM_IMAGE, RANDOM_USER);

        assertNull(userImage.getId());
        assertEquals(userImage.getImage(), RANDOM_IMAGE);
        assertEquals(userImage.getUser(), RANDOM_USER);
    }
}
