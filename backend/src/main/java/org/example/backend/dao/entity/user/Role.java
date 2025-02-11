package org.example.backend.dao.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//Entity for storing role data
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Unique role name, must be created with ROLE_ prefix and with only capital letters
    @Column(nullable = false, unique = true)
    private String name;

    //Privileges assigned to the role
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    )
    private List<Privilege> privileges;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;

    public Role(String name, List<Privilege> privileges) {
        this.name = name;
        this.privileges = privileges;
    }
}
