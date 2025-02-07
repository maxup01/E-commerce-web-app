package org.example.backend.dao.entity.image;

import org.example.backend.dao.entity.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductMainImageTest {

    private final byte[] RANDOM_IMAGE = new byte[10];
    private final Product RANDOM_PRODUCT = new Product();

    @Test
    public void testOfConstructorWithImageArgument(){

        ProductMainImage productMainImage = new ProductMainImage(RANDOM_IMAGE);

        assertNull(productMainImage.getId());
        assertEquals(productMainImage.getImage(), RANDOM_IMAGE);
        assertNull(productMainImage.getProduct());
    }

    @Test
    public void testOfConstructorWithImageAndProductArguments(){

        ProductMainImage productMainImage = new ProductMainImage(RANDOM_IMAGE, RANDOM_PRODUCT);

        assertNull(productMainImage.getId());
        assertEquals(productMainImage.getImage(), RANDOM_IMAGE);
        assertEquals(productMainImage.getProduct(), RANDOM_PRODUCT);
    }
}
