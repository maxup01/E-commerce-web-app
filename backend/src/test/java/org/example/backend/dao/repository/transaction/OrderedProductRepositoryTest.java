package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderedProductRepositoryTest {

    private final String RANDOM_PHRASE_LOWER_CASE = "random";
    private final String RANDOM_PRODUCT_NAME = "Random name";
    private final String DIFFERENT_PRODUCT_NAME = "Different name";
    private final String RANDOM_EAN_CODE = "6234628742679";
    private final String DIFFERENT_EAN_CODE = "62346287431679";
    private final String RANDOM_TYPE_LOWER_CASE = "Random type";
    private final String DIFFERENT_TYPE_LOWER_CASE = "Different type";
    private final String RANDOM_DESCRIPTION = "Random description";
    private final Double RANDOM_REGULAR_PRICE = 99.10;
    private final Double RANDOM_CURRENT_PRICE = 100.99;
    private final Stock RANDOM_STOCK = new Stock(10L);
    private final Stock DIFFERENT_STOCK = new Stock(10L);
    private final byte[] RANDOM_IMAGE = new byte[12];
    private final ProductMainImage RANDOM_PRODUCT_MAIN_IMAGE = new ProductMainImage(RANDOM_IMAGE);
    private final ProductMainImage DIFFERENT_PRODUCT_MAIN_IMAGE = new ProductMainImage(RANDOM_IMAGE);
    private final Long RANDOM_QUANTITY = 10L;
    private final Long DIFFERENT_QUANTITY = 31L;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Test
    public void testOfSave(){

        Product product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_PRODUCT_MAIN_IMAGE);

        OrderedProduct orderedProduct = new OrderedProduct(product, RANDOM_QUANTITY, product.getCurrentPrice());

        assertDoesNotThrow(() -> orderedProductRepository.save(orderedProduct));
    }

    @Test
    public void testOfGetAllQuantityOfOrderedProductsAndRevenue(){

        Product product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_PRODUCT_MAIN_IMAGE);
        productRepository.save(product);

        OrderedProduct orderedProduct1 = new OrderedProduct(product, RANDOM_QUANTITY, product.getCurrentPrice());
        orderedProductRepository.save(orderedProduct1);

        OrderedProduct orderedProduct2 = new OrderedProduct(product, RANDOM_QUANTITY, product.getCurrentPrice());
        orderedProductRepository.save(orderedProduct2);

        List<Object[]> quantityAndRevenue =  orderedProductRepository.getAllQuantityOfOrderedProductsAndRevenue();

        Long quantity = (Long) quantityAndRevenue.get(0)[0];
        Double revenue = (Double) quantityAndRevenue.get(0)[1];

        assertEquals(quantity, RANDOM_QUANTITY * 2);
        assertEquals(revenue, RANDOM_QUANTITY * 2 * product.getCurrentPrice());
    }

    @Test
    public void testOfGetAllTypesAndTheirOrderedQuantityAndRevenue(){

        Product product1 = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_PRODUCT_MAIN_IMAGE);
        productRepository.save(product1);

        Product product2 = new Product(RANDOM_PRODUCT_NAME, DIFFERENT_EAN_CODE, DIFFERENT_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, DIFFERENT_STOCK, DIFFERENT_PRODUCT_MAIN_IMAGE);
        productRepository.save(product2);

        OrderedProduct orderedProduct1 = new OrderedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice());
        orderedProductRepository.save(orderedProduct1);

        OrderedProduct orderedProduct2 = new OrderedProduct(product2, DIFFERENT_QUANTITY, product2.getCurrentPrice());
        orderedProductRepository.save(orderedProduct2);

        List<Object[]> list = orderedProductRepository.getAllTypesAndTheirOrderedQuantityAndRevenue();

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        list.forEach(row -> {
            map1.put((String) row[0], (Long) row[1]);
            map2.put((String) row[0], (Double) row[2]);
        });

        assertEquals(map1.size(), 2);
        assertEquals(map2.size(), 2);
        assertEquals(map1.get(RANDOM_TYPE_LOWER_CASE), RANDOM_QUANTITY);
        assertEquals(map1.get(DIFFERENT_TYPE_LOWER_CASE), DIFFERENT_QUANTITY);
        assertEquals(map2.get(RANDOM_TYPE_LOWER_CASE), RANDOM_QUANTITY * product1.getCurrentPrice());
        assertEquals(map2.get(DIFFERENT_TYPE_LOWER_CASE), DIFFERENT_QUANTITY * product2.getCurrentPrice());
    }

    @Test
    public void testOfGetAllTypesAndTheirRevenueOfOrderedProducts(){

        Product product1 = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_PRODUCT_MAIN_IMAGE);
        productRepository.save(product1);

        Product product2 = new Product(DIFFERENT_PRODUCT_NAME, DIFFERENT_EAN_CODE, DIFFERENT_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, DIFFERENT_STOCK, DIFFERENT_PRODUCT_MAIN_IMAGE);
        productRepository.save(product2);

        OrderedProduct orderedProduct1 = new OrderedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice());
        orderedProductRepository.save(orderedProduct1);

        OrderedProduct orderedProduct2 = new OrderedProduct(product2, DIFFERENT_QUANTITY, product2.getCurrentPrice());
        orderedProductRepository.save(orderedProduct2);

        List<Object[]> list = orderedProductRepository.getProductsAndTheirOrderedQuantityAndRevenueByPhrase(RANDOM_PHRASE_LOWER_CASE);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        list.forEach(row -> {
            Product product = (Product) row[0];
            map1.put(product.getName(), (Long) row[1]);
            map2.put(product.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 1);
        assertEquals(map2.size(), 1);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * product1.getCurrentPrice());
    }

    @Test
    public void testOfGetProductsAndTheirOrderedQuantityAndRevenueByType(){

        Product product1 = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_PRODUCT_MAIN_IMAGE);
        productRepository.save(product1);

        Product product2 = new Product(RANDOM_PRODUCT_NAME, DIFFERENT_EAN_CODE, DIFFERENT_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, DIFFERENT_STOCK, DIFFERENT_PRODUCT_MAIN_IMAGE);
        productRepository.save(product2);

        OrderedProduct orderedProduct1 = new OrderedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice());
        orderedProductRepository.save(orderedProduct1);

        OrderedProduct orderedProduct2 = new OrderedProduct(product2, DIFFERENT_QUANTITY, product2.getCurrentPrice());
        orderedProductRepository.save(orderedProduct2);

        List<Object[]> list = orderedProductRepository.getProductsAndTheirOrderedQuantityAndRevenueByType(RANDOM_TYPE_LOWER_CASE);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        list.forEach(row -> {
            Product product = (Product) row[0];
            map1.put(product.getName(), (Long) row[1]);
            map2.put(product.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 1);
        assertEquals(map2.size(), 1);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * product1.getCurrentPrice());
    }
}
