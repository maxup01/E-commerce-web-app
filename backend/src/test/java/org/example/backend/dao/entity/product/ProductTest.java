package org.example.backend.dao.entity.product;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductTest {

    private final String RANDOM_EAN_CODE = "0799439112766";
    private final String RANDOM_NAME = "Random name";
    private final String RANDOM_TYPE = "Random type";
    private final String RANDOM_DESCRIPTION = "Random description";
    private final Double RANDOM_REGULAR_PRICE = 100.00;
    private final Double RANDOM_CURRENT_PRICE = 50.00;
    private final Stock RANDOM_STOCK = new Stock();
    private final ProductMainImage RANDOM_MAIN_IMAGE = new ProductMainImage();
    private final List<ProductPageImage> RANDOM_IMAGE_LIST = List.of(new ProductPageImage(), new ProductPageImage());

    @Test
    public void testOfConstructorWithEANCodeAndNameAndTypeAndDescriptionAndRegularPriceAndCurrentPriceAndStockArguments(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE);

        assertNull(product.getId());
        assertEquals(product.getEANCode(), RANDOM_EAN_CODE);
        assertEquals(product.getName(), RANDOM_NAME);
        assertEquals(product.getType(), RANDOM_TYPE);
        assertEquals(product.getDescription(), RANDOM_DESCRIPTION);
        assertEquals(product.getRegularPrice(), RANDOM_REGULAR_PRICE);
        assertEquals(product.getCurrentPrice(), RANDOM_CURRENT_PRICE);
        assertEquals(product.getMainImage(), RANDOM_MAIN_IMAGE);
        assertEquals(product.getStock(), RANDOM_STOCK);
    }

    @Test
    public void testOfConstructorWithEANCodeAndNameAndTypeAndDescriptionAndRegularPriceAndCurrentPriceAndStockAndProductImageArguments(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);

        assertNull(product.getId());
        assertEquals(product.getEANCode(), RANDOM_EAN_CODE);
        assertEquals(product.getName(), RANDOM_NAME);
        assertEquals(product.getType(), RANDOM_TYPE);
        assertEquals(product.getDescription(), RANDOM_DESCRIPTION);
        assertEquals(product.getRegularPrice(), RANDOM_REGULAR_PRICE);
        assertEquals(product.getCurrentPrice(), RANDOM_CURRENT_PRICE);
        assertEquals(product.getStock(), RANDOM_STOCK);
        assertEquals(product.getMainImage(), RANDOM_MAIN_IMAGE);
        assertEquals(product.getPageImages(), RANDOM_IMAGE_LIST);
    }
}
