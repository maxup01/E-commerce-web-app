package org.example.backend.dao.entity.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StockTest {

    private final Long RANDOM_STOCK_QUANTITY = 10L;
    private final Product RANDOM_STOCK_PRODUCT = new Product();

    @Test
    public void testOfConstructorWithQuantityArgument(){

        Stock stock = new Stock(RANDOM_STOCK_QUANTITY);

        assertNull(stock.getId());
        assertEquals(stock.getQuantity(), RANDOM_STOCK_QUANTITY);
        assertNull(stock.getProduct());
    }

    @Test
    public void testOfConstructorWithQuantityAndProductArguments(){

        Stock stock = new Stock(RANDOM_STOCK_QUANTITY, RANDOM_STOCK_PRODUCT);

        assertNull(stock.getId());
        assertEquals(stock.getQuantity(), RANDOM_STOCK_QUANTITY);
        assertEquals(stock.getProduct(), RANDOM_STOCK_PRODUCT);
    }
}
