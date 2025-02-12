package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserImageRepository extends JpaRepository<UserImage, UUID> {}
