package org.example.backend.dao.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//Entity for storing privilege data
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Unique privilege name, must be created with suffix _PRIVILEGE and with only capital letters
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "privileges", fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<Role> roles;

    public Privilege(String name) {
        this.name = name;
    }
}
