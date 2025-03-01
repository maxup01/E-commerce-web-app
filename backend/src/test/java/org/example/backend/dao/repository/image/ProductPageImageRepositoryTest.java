package org.example.backend.dao.repository.image;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ProductPageImageRepositoryTest {

    private final String RANDOM_EAN_CODE = "0799439112766";
    private final String DIFFERENT_EAN_CODE = "07994391127222";
    private final String RANDOM_NAME = "Random name";
    private final String DIFFERENT_NAME_LOWER_CASE = "different name";
    private final String PHRASE_OF_RANDOM_NAME_LOWER_CASE = "random";
    private final String RANDOM_TYPE_LOWER_CASE = "random type";
    private final String DIFFERENT_RANDOM_TYPE_LOWER_CASE = "different type";
    private final String RANDOM_DESCRIPTION = "Random description";
    private final Double RANDOM_REGULAR_PRICE = 100.00;
    private final Double RANDOM_CURRENT_PRICE = 80.00;
    private final Double LOWER_PRICE_THAN_CURRENT_PRICE = 50.00;
    private final Double GREATER_PRICE_THAN_CURRENT_PRICE_1 = 100.00;
    private final Double GREATER_PRICE_THAN_CURRENT_PRICE_2 = 150.00;
    private final Stock RANDOM_STOCK = new Stock(5L);
    private final Stock DIFFERENT_STOCK = new Stock(2L);
    private final Long SUM_OF_STOCKS_QUANTITY = 7L;
    private final byte[] RANDOM_IMAGE = new byte[10];

    @Autowired
    private ProductPageImageRepository productPageImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testOfSaveSave(){

        ProductPageImage productPageImage = new ProductPageImage(RANDOM_IMAGE);

        assertDoesNotThrow(() -> {
            productPageImageRepository.save(productPageImage);
        });
    }

    @Test
    public void testOfFindByProductId(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                10, 10, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK,
                new ProductMainImage(RANDOM_IMAGE));
        product = productRepository.save(product);
        ProductPageImage productPageImage = new ProductPageImage(RANDOM_IMAGE, product);
        productPageImageRepository.save(productPageImage);
        product.setPageImages(List.of(productPageImage));


        List<ProductPageImage> productPageImages = productPageImageRepository.findByProductId(product.getId());

        assertEquals(productPageImages.size(), 1);
    }
}
