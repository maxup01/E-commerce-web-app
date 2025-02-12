package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductMainImageRepository extends JpaRepository<ProductMainImage, UUID> {}
