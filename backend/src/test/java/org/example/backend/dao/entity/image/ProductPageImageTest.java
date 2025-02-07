package org.example.backend.dao.entity.image;

import org.example.backend.dao.entity.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductPageImageTest {

    private final byte[] RANDOM_IMAGE = new byte[10];
    private final Product RANDOM_PRODUCT = new Product();

    @Test
    public void testOfConstructorWithImageArgument() {

        ProductPageImage productPageImage = new ProductPageImage(RANDOM_IMAGE);

        assertNull(productPageImage.getId());
        assertEquals(productPageImage.getImage(), RANDOM_IMAGE);
        assertNull(productPageImage.getProduct());
    }

    @Test
    public void testOfConstructorWithImageAndProductArguments() {

        ProductPageImage productPageImage = new ProductPageImage(RANDOM_IMAGE, RANDOM_PRODUCT);

        assertNull(productPageImage.getId());
        assertEquals(productPageImage.getImage(), RANDOM_IMAGE);
        assertEquals(productPageImage.getProduct(), RANDOM_PRODUCT);
    }
}
