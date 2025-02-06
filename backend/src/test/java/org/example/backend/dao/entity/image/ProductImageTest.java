package org.example.backend.dao.entity.image;

import org.example.backend.dao.entity.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductImageTest {

    private final byte[] RANDOM_IMAGE = new byte[10];
    private final boolean RANDOM_IS_MAIN_VALUE = true;
    private final Product RANDOM_PRODUCT = new Product();

    @Test
    public void testOfConstructorWithImageArgument() {

        ProductImage productImage = new ProductImage(RANDOM_IMAGE);

        assertNull(productImage.getId());
        assertEquals(productImage.getImage(), RANDOM_IMAGE);
        assertNull(productImage.getProduct());
    }

    @Test
    public void testOfConstructorWithImageAndProductArguments() {

        ProductImage productImage = new ProductImage(RANDOM_IMAGE, RANDOM_IS_MAIN_VALUE, RANDOM_PRODUCT);

        assertNull(productImage.getId());
        assertEquals(productImage.getImage(), RANDOM_IMAGE);
        assertEquals(productImage.isMainImage(), RANDOM_IS_MAIN_VALUE);
        assertEquals(productImage.getProduct(), RANDOM_PRODUCT);
    }
}
