package org.example.backend.dao.repository.product;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    private final String RANDOM_EAN_CODE = "0799439112766";
    private final String DIFFERENT_EAN_CODE = "07994391127222";
    private final String RANDOM_NAME = "Random name";
    private final String DIFFERENT_NAME_LOWER_CASE = "different name";
    private final String PHRASE_OF_RANDOM_NAME_LOWER_CASE = "random";
    private final String RANDOM_TYPE_LOWER_CASE = "random type";
    private final String DIFFERENT_RANDOM_TYPE_LOWER_CASE = "different type";
    private final String RANDOM_DESCRIPTION = "Random description";
    private final Double RANDOM_REGULAR_PRICE = 100.00;
    private final Double RANDOM_CURRENT_PRICE = 80.00;
    private final Double LOWER_PRICE_THAN_CURRENT_PRICE = 50.00;
    private final Double GREATER_PRICE_THAN_CURRENT_PRICE_1 = 100.00;
    private final Double GREATER_PRICE_THAN_CURRENT_PRICE_2 = 150.00;
    private final Stock RANDOM_STOCK = new Stock(5L);
    private final Stock DIFFERENT_STOCK = new Stock(2L);
    private final Long SUM_OF_STOCKS_QUANTITY = 7L;
    private final ProductMainImage RANDOM_MAIN_IMAGE = new ProductMainImage(new byte[10]);
    private final ProductMainImage DIFFERENT_MAIN_IMAGE = new ProductMainImage(new byte[13]);
    private final List<ProductPageImage> RANDOM_IMAGE_LIST = List.of(new ProductPageImage(new byte[10]), new ProductPageImage(new byte[10]));
    private final List<ProductPageImage> DIFFERENT_IMAGE_LIST = List.of(new ProductPageImage(new byte[10]), new ProductPageImage(new byte[10]));
    private final Integer RANDOM_HEIGHT = 1000;
    private final Integer RANDOM_WIDTH = 1000;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {

        product1 = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);

        product2 = new Product(DIFFERENT_NAME_LOWER_CASE, DIFFERENT_EAN_CODE, DIFFERENT_RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, DIFFERENT_STOCK, DIFFERENT_MAIN_IMAGE, DIFFERENT_IMAGE_LIST);
        productRepository.save(product2);
    }

    @Test
    public void testOfSave(){

        assertDoesNotThrow(() -> {
            productRepository.save(product1);
            entityManager.flush();
        });

        //This product is created incorrectly cause of the same stock, pageImages, mainImage and EAN code
        // as product1, it violates @OneToOne and @ManyToOne relationships and uniqueness of EAC code fields
        Product incorrectProduct = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);

        assertThrows(ConstraintViolationException.class, () -> {
            productRepository.save(incorrectProduct);
            entityManager.flush();
        });

        assertNotNull(product1.getId());
        assertNotNull(product1.getStock().getId());
        assertNotNull(product1.getMainImage().getId());
        assertNotNull(product1.getPageImages().get(0).getId());
        assertNotNull(product1.getPageImages().get(1).getId());
    }

    @Test
    public void testOfFindByEANCode(){

        productRepository.save(product1);

        Product foundProduct = productRepository.findByEANCode(RANDOM_EAN_CODE);

        assertNotNull(foundProduct);
        assertEquals(foundProduct.getName(), RANDOM_NAME);
        assertEquals(foundProduct.getEANCode(), RANDOM_EAN_CODE);
        assertEquals(foundProduct.getType(), RANDOM_TYPE_LOWER_CASE);
        assertEquals(foundProduct.getDescription(), RANDOM_DESCRIPTION);
        assertEquals(foundProduct.getHeight(), RANDOM_HEIGHT);
        assertEquals(foundProduct.getWidth(), RANDOM_WIDTH);
        assertEquals(foundProduct.getRegularPrice(), RANDOM_REGULAR_PRICE);
        assertEquals(foundProduct.getCurrentPrice(), RANDOM_CURRENT_PRICE);
    }

    @Test
    public void testOffFindByType(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);
        productRepository.save(product);

        List<Product> products = productRepository.findByType(RANDOM_TYPE_LOWER_CASE);

        assertEquals(products.size(), 1);
    }

    @Test
    public void testOfFindByPhrase(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);
        productRepository.save(product);

        List<Product> products = productRepository.findByPhrase(PHRASE_OF_RANDOM_NAME_LOWER_CASE);

        assertEquals(products.size(), 1);
    }

    @Test
    public void testOfFindByPriceRange(){

        List<Product> products = productRepository.findByPriceRange(LOWER_PRICE_THAN_CURRENT_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_1);
        List<Product> notFoundProducts = productRepository.findByPriceRange(GREATER_PRICE_THAN_CURRENT_PRICE_1, GREATER_PRICE_THAN_CURRENT_PRICE_2);

        assertEquals(products.size(), 1);
        assertEquals(notFoundProducts.size(), 0);
    }

    @Test
    public void testOfFindByPhraseAndType(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);
        productRepository.save(product);

        List<Product> products = productRepository
                .findByPhraseAndType(PHRASE_OF_RANDOM_NAME_LOWER_CASE, RANDOM_TYPE_LOWER_CASE);
        List<Product> notFoundedProductList = productRepository
                .findByPhraseAndType(PHRASE_OF_RANDOM_NAME_LOWER_CASE, DIFFERENT_RANDOM_TYPE_LOWER_CASE);

        assertEquals(products.size(), 1);
        assertEquals(notFoundedProductList.size(), 0);
    }

    @Test
    public void testOfFindByPhraseAndPriceRange(){

        Product product = new Product(RANDOM_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_MAIN_IMAGE, RANDOM_IMAGE_LIST);
        productRepository.save(product);

        List<Product> products = productRepository
                .findByPhraseAndPriceRange(PHRASE_OF_RANDOM_NAME_LOWER_CASE,
                        LOWER_PRICE_THAN_CURRENT_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_1);
        List<Product> notFoundedProductList = productRepository
                .findByPhraseAndPriceRange(PHRASE_OF_RANDOM_NAME_LOWER_CASE, GREATER_PRICE_THAN_CURRENT_PRICE_1,
                        GREATER_PRICE_THAN_CURRENT_PRICE_2);

        assertEquals(products.size(), 1);
        assertEquals(notFoundedProductList.size(), 0);
    }

    @Test
    public void testOffFindByPhraseAndTypeAndPriceRange(){

        productRepository.save(product1);

        List<Product> products = productRepository.findByPhraseAndTypeAndPriceRanges(PHRASE_OF_RANDOM_NAME_LOWER_CASE,
                RANDOM_TYPE_LOWER_CASE, LOWER_PRICE_THAN_CURRENT_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_1);

        List<Product> productsNotFound = productRepository.findByPhraseAndTypeAndPriceRanges(PHRASE_OF_RANDOM_NAME_LOWER_CASE,
                RANDOM_TYPE_LOWER_CASE, GREATER_PRICE_THAN_CURRENT_PRICE_1, GREATER_PRICE_THAN_CURRENT_PRICE_2);

        assertEquals(products.size(), 1);
        assertEquals(productsNotFound.size(), 0);
    }

    @Test
    public void testOfShowOnSale(){

        List<Product> products = productRepository.showOnSale();

        assertEquals(products.size(), 1);
    }

    @Test
    public void testOfGetTotalQuantityOfProducts(){

        productRepository.save(product1);

        Long quantity = productRepository.getTotalQuantityOfProducts();

        assertEquals(quantity, SUM_OF_STOCKS_QUANTITY);
    }

    @Test
    public void testOfGetTypesAndQuantityOfProductsWithThisTypes1(){

        productRepository.save(product1);

        List<Object[]> list = productRepository.getTypesAndQuantityOfProductsWithThisTypes();

        HashMap<String, Long> map = new HashMap<>();

        list.forEach(row -> {
            map.put((String) row[0], (Long) row[1]);
        });

        assertEquals(list.size(), 2);
        assertEquals(map.get(RANDOM_TYPE_LOWER_CASE), RANDOM_STOCK.getQuantity());
        assertEquals(map.get(DIFFERENT_RANDOM_TYPE_LOWER_CASE), DIFFERENT_STOCK.getQuantity());
    }

    @Test
    public void testOftestOfGetTypesAndQuantityOfProductsWithThisTypes2(){

        productRepository.save(product1);

        List<Object[]> list = productRepository.getTypesAndQuantityOfProductsWithThisTypes();

        HashMap<String, Long> map = new HashMap<>();

        list.forEach(row -> {
            map.put((String) row[0], (Long) row[1]);
        });

        assertEquals(list.size(), 2);
        assertEquals(map.get(RANDOM_TYPE_LOWER_CASE), RANDOM_STOCK.getQuantity());
        assertEquals(map.get(DIFFERENT_RANDOM_TYPE_LOWER_CASE), DIFFERENT_STOCK.getQuantity());
    }

    @Test
    public void testOfGetProductsNameAndRelatedQuantityByPhrase(){

        productRepository.save(product1);

        List<Object[]> list = productRepository.getProductsAndRelatedQuantityByPhrase(PHRASE_OF_RANDOM_NAME_LOWER_CASE);

        HashMap<String, Long> map = new HashMap<>();

        list.forEach(row -> {
            map.put(((Product) row[0]).getName(), (Long) row[1]);
        });

        assertEquals(map.size(), 1);
        assertEquals(map.get(RANDOM_NAME), RANDOM_STOCK.getQuantity());
    }
}
