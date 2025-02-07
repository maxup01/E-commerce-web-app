package org.example.backend.dao.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

//Entity for storing profile image for user
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "profileImage")
    private User user;

    public UserImage(byte[] image) {
        this.image = image;
    }

    public UserImage(byte[] image, User user) {
        this.image = image;
        this.user = user;
    }
}
