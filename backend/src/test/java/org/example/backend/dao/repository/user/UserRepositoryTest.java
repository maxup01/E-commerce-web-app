package org.example.backend.dao.repository.user;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.image.UserImage;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    private final String RANDOM_FIRST_NAME = "FirstName";
    private final String DIFFERENT_FIRST_NAME = "DifferentFirstName";
    private final String RANDOM_LAST_NAME = "LastName";
    private final String DIFFERENT_LAST_NAME = "DifferentLastName";
    private final String RANDOM_EMAIL = "email@email.com";
    private final String DIFFERENT_EMAIL = "differentEmail@email.com";
    private final String RANDOM_PASSWORD = "RandomPassword";
    private final String DIFFERENT_PASSWORD = "DifferentPassword";
    private final LocalDate RANDOM_DATE = LocalDate.of(1950, 1, 1);
    private final LocalDate DIFFERENT_DATE = LocalDate.of(1930, 1, 1);
    private final byte[] RANDOM_IMAGE = new byte[12];
    private final byte[] DIFFERENT_IMAGE = new byte[12];
    private final String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    private final String RANDOM_ROLE_NAME = "ROLE_RANDOM";

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testOfSave(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        UserImage userImage = new UserImage(RANDOM_IMAGE);
        User randomUser = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE, role, userImage);

        assertDoesNotThrow(() -> {
            userRepository.save(randomUser);
            entityManager.flush();
        });

        UserImage userImage2 = new UserImage(DIFFERENT_IMAGE);
        User randomUser2 = new User(DIFFERENT_FIRST_NAME, DIFFERENT_LAST_NAME, RANDOM_EMAIL, DIFFERENT_PASSWORD,
                DIFFERENT_DATE, role, userImage2);

        //Email is not unique in randomUser2
        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(randomUser2);
            entityManager.flush();
        });

        User randomUser3 = new User(DIFFERENT_FIRST_NAME, DIFFERENT_LAST_NAME, DIFFERENT_EMAIL, DIFFERENT_PASSWORD,
                DIFFERENT_DATE, role, userImage);

        //UserImage in randomUser3 is the same as in randomUser
        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(randomUser3);
            entityManager.flush();
        });
    }

    @Test
    public void testOfFindByEmail(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        UserImage userImage = new UserImage(RANDOM_IMAGE);
        User randomUser = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE, role, userImage);
        userRepository.save(randomUser);

        User foundUser = userRepository.findByEmail(RANDOM_EMAIL);

        assertNotNull(foundUser.getId());
        assertEquals(randomUser.getFirstName(), RANDOM_FIRST_NAME);
        assertEquals(randomUser.getLastName(), RANDOM_LAST_NAME);
        assertEquals(randomUser.getEmail(), RANDOM_EMAIL);
        assertEquals(randomUser.getPassword(), RANDOM_PASSWORD);
        assertEquals(randomUser.getBirthDate(), RANDOM_DATE);
        assertEquals(randomUser.getRole(), role);
        assertEquals(randomUser.getProfileImage(), userImage);
    }

    @Test
    public void testOfFindAllUsersByFirstNameAndLastName(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        UserImage userImage = new UserImage(RANDOM_IMAGE);
        User randomUser = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE, role, userImage);
        userRepository.save(randomUser);

        List<User> foundUsers = userRepository.findAllUsersByFirstNameAndLastName(RANDOM_FIRST_NAME, RANDOM_LAST_NAME);
        User foundUser = foundUsers.get(0);

        assertNotNull(foundUser.getId());
        assertEquals(randomUser.getFirstName(), RANDOM_FIRST_NAME);
        assertEquals(randomUser.getLastName(), RANDOM_LAST_NAME);
        assertEquals(randomUser.getEmail(), RANDOM_EMAIL);
        assertEquals(randomUser.getPassword(), RANDOM_PASSWORD);
        assertEquals(randomUser.getBirthDate(), RANDOM_DATE);
        assertEquals(randomUser.getRole(), role);
        assertEquals(randomUser.getProfileImage(), userImage);
    }

    @Test
    public void testOfFindAllUsersByRoleName(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        UserImage userImage = new UserImage(RANDOM_IMAGE);
        User randomUser = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE, role, userImage);
        userRepository.save(randomUser);

        List<User> foundUsers = userRepository.findAllUsersByRoleName(RANDOM_ROLE_NAME);
        User foundUser = foundUsers.get(0);

        assertNotNull(foundUser.getId());
        assertEquals(randomUser.getFirstName(), RANDOM_FIRST_NAME);
        assertEquals(randomUser.getLastName(), RANDOM_LAST_NAME);
        assertEquals(randomUser.getEmail(), RANDOM_EMAIL);
        assertEquals(randomUser.getPassword(), RANDOM_PASSWORD);
        assertEquals(randomUser.getBirthDate(), RANDOM_DATE);
        assertEquals(randomUser.getRole(), role);
        assertEquals(randomUser.getProfileImage(), userImage);
    }

    @Test
    public void testOfFindByUserAge(){

        Privilege privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        Role role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        UserImage userImage = new UserImage(RANDOM_IMAGE);
        User randomUser = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE, role, userImage);
        userRepository.save(randomUser);

        List<User> foundUsers = userRepository.findByUserAge(1, 100);
        User foundUser = foundUsers.get(0);

        assertNotNull(foundUser.getId());
        assertEquals(randomUser.getFirstName(), RANDOM_FIRST_NAME);
        assertEquals(randomUser.getLastName(), RANDOM_LAST_NAME);
        assertEquals(randomUser.getEmail(), RANDOM_EMAIL);
        assertEquals(randomUser.getPassword(), RANDOM_PASSWORD);
        assertEquals(randomUser.getBirthDate(), RANDOM_DATE);
        assertEquals(randomUser.getRole(), role);
        assertEquals(randomUser.getProfileImage(), userImage);
    }
}
