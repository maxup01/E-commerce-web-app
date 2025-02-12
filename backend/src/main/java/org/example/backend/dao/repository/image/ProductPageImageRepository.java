package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.ProductPageImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

//Repository exists only for persisting images and merge them with products
public interface ProductPageImageRepository extends JpaRepository <ProductPageImage, UUID> {}
