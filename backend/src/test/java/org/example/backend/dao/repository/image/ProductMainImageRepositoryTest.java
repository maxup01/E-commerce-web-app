package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
public class ProductMainImageRepositoryTest {

    private final byte[] RANDOM_IMAGE = new byte[12];

    @Autowired
    private ProductMainImageRepository productMainImageRepository;

    @Test
    public void testOfSave(){

        ProductMainImage productMainImage = new ProductMainImage(RANDOM_IMAGE);

        assertDoesNotThrow(() -> {
            productMainImageRepository.save(productMainImage);
        });
    }
}
