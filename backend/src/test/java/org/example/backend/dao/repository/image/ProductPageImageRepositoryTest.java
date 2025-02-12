package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.ProductPageImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
public class ProductPageImageRepositoryTest {

    private final byte[] RANDOM_IMAGE = new byte[10];

    @Autowired
    private ProductPageImageRepository productPageImageRepository;

    @Test
    public void testOfSaveSave(){

        ProductPageImage productPageImage = new ProductPageImage(RANDOM_IMAGE);

        assertDoesNotThrow(() -> {
            productPageImageRepository.save(productPageImage);
        });
    }
}
