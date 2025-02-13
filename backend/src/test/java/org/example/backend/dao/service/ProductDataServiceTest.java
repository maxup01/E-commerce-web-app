package org.example.backend.dao.service;

import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductDataServiceTest {

    private final UUID ID_OF_USER_WHICH_EXIST = UUID.randomUUID();
    private final UUID ID_OF_USER_WHICH_NOT_EXIST = UUID.randomUUID();
    private final String RANDOM_TYPE = "random";
    private final String TYPE_THAT_NOT_EXIST = "notexist";

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    private ProductDataService productDataService;

    private Product product;
    private List<Product> list_of_products;

    @BeforeEach
    public void setUp() {
        product = new Product();
        list_of_products = new ArrayList<>();
        list_of_products.add(product);
    }

    @Test
    public void testOfGetProductById(){

        when(productRepository.findById(ID_OF_USER_WHICH_EXIST)).thenReturn(Optional.ofNullable(product));
        when(productRepository.findById(ID_OF_USER_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductById(null);
        });

        Exception secondException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.getProductById(ID_OF_USER_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductById(ID_OF_USER_WHICH_EXIST);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Product with id " + ID_OF_USER_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetProductsByType(){

        when(productRepository.findByType(RANDOM_TYPE)).thenReturn(list_of_products);
        when(productRepository.findByType(TYPE_THAT_NOT_EXIST)).thenReturn(List.of());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByType(null);
        });

        Exception secondException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.getProductsByType(TYPE_THAT_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByType(RANDOM_TYPE);
        });

        assertEquals(firstException.getMessage(), "Null argument: type");
        assertEquals(secondException.getMessage(), "Products with type " + TYPE_THAT_NOT_EXIST + " not found");
    }
}
