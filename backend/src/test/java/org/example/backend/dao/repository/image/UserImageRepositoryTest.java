package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.UserImage;
import org.example.backend.dao.entity.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
public class UserImageRepositoryTest {

    private final byte[] RANDOM_IMAGE = new byte[12];

    @Autowired
    private UserImageRepository userImageRepository;

    @Test
    public void testOfSave(){

        UserImage userImage = new UserImage(RANDOM_IMAGE);

        assertDoesNotThrow(() -> {
            userImageRepository.save(userImage);
        });
    }
}
