package org.example.backend.dao.entity.product;

import org.example.backend.dao.entity.image.ProductImage;
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
    private final List<Stock> RANDOM_STOCK_LIST = List.of(new Stock(), new Stock());
    private final List<ProductImage> RANDOM_IMAGE_LIST = List.of(new ProductImage(), new ProductImage());

    @Test
    public void testOfConstructorWithEANCodeAndNameAndTypeAndDescriptionAndRegularPriceAndCurrentPriceAndStockArguments(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK_LIST);

        assertNull(product.getId());
        assertEquals(product.getEANCode(), RANDOM_EAN_CODE);
        assertEquals(product.getName(), RANDOM_NAME);
        assertEquals(product.getType(), RANDOM_TYPE);
        assertEquals(product.getDescription(), RANDOM_DESCRIPTION);
        assertEquals(product.getRegularPrice(), RANDOM_REGULAR_PRICE);
        assertEquals(product.getCurrentPrice(), RANDOM_CURRENT_PRICE);
        assertEquals(product.getStocks(), RANDOM_STOCK_LIST);
    }

    @Test
    public void testOfConstructorWithEANCodeAndNameAndTypeAndDescriptionAndRegularPriceAndCurrentPriceAndStockAndProductImageArguments(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK_LIST, RANDOM_IMAGE_LIST);

        assertNull(product.getId());
        assertEquals(product.getEANCode(), RANDOM_EAN_CODE);
        assertEquals(product.getName(), RANDOM_NAME);
        assertEquals(product.getType(), RANDOM_TYPE);
        assertEquals(product.getDescription(), RANDOM_DESCRIPTION);
        assertEquals(product.getRegularPrice(), RANDOM_REGULAR_PRICE);
        assertEquals(product.getCurrentPrice(), RANDOM_CURRENT_PRICE);
        assertEquals(product.getStocks(), RANDOM_STOCK_LIST);
        assertEquals(product.getImages(), RANDOM_IMAGE_LIST);
    }
}
