package org.example.backend.dao.repository.user;

import org.example.backend.dao.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    List<User> findAllUsersByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findAllUsersByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User AS u WHERE TIMESTAMPDIFF(YEAR, u.birthDate, CURDATE()) >= :minAge" +
            " AND TIMESTAMPDIFF(YEAR, u.birthDate, CURDATE()) <= :maxAge")
    List<User> findByUserAge(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
}
