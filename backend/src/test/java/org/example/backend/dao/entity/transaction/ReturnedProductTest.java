package org.example.backend.dao.entity.transaction;

import org.example.backend.dao.entity.product.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReturnedProductTest {

    private final Product RANDOM_PRODUCT = new Product();
    private final Long RANDOM_QUANTITY = 1L;
    private final Double RANDOM_PRICE_PER_UNIT = 50.00;

    @Test
    public void testOfConstructorWithArgumentsProductAndQuantityAndPricePerUnit() {

        ReturnedProduct returnedProduct = new ReturnedProduct(RANDOM_PRODUCT, RANDOM_QUANTITY, RANDOM_PRICE_PER_UNIT);

        assertNull(returnedProduct.getId());
        assertEquals(returnedProduct.getProduct(), RANDOM_PRODUCT);
        assertEquals(returnedProduct.getQuantity(), RANDOM_QUANTITY);
        assertEquals(returnedProduct.getPricePerUnit(), RANDOM_PRICE_PER_UNIT);
    }
}
