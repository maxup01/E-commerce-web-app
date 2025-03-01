package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.ProductPageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

//Repository exists only for persisting images and merge them with products
public interface ProductPageImageRepository extends JpaRepository <ProductPageImage, UUID> {

    @Query("SELECT p FROM ProductPageImage AS p WHERE p.product.id = :productId")
    List<ProductPageImage> findByProductId(UUID productId);
}
