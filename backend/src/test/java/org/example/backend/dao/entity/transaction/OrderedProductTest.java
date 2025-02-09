package org.example.backend.dao.entity.transaction;

import org.example.backend.dao.entity.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderedProductTest {

    private final Product RANDOM_PRODUCT = new Product();
    private final Long RANDOM_QUANTITY = 1L;
    private final Double RANDOM_PRICE_PER_UNIT = 50.00;

    @Test
    public void testOfConstructorWithProductAndQuantityAndPricePerUnitArguments(){

        OrderedProduct orderedProduct = new OrderedProduct(RANDOM_PRODUCT, RANDOM_QUANTITY, RANDOM_PRICE_PER_UNIT);

        assertNull(orderedProduct.getId());
        assertEquals(orderedProduct.getProduct(), RANDOM_PRODUCT);
        assertEquals(orderedProduct.getQuantity(), RANDOM_QUANTITY);
        assertEquals(orderedProduct.getPricePerUnit(), RANDOM_PRICE_PER_UNIT);
    }
}
