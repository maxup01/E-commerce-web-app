package org.example.backend.model;

import lombok.*;
import org.example.backend.dao.entity.user.User;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private RoleModel role;

    public static UserModel fromUser(User user) {

        return new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword(), user.getBirthDate(), RoleModel.fromRole(user.getRole()));
    }
}
