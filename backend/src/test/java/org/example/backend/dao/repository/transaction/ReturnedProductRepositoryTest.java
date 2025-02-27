package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.*;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.logistic.AddressRepository;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.enumerated.ReturnCause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ReturnedProductRepositoryTest {

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
    private final Date DATE_BEFORE = new Date(0);
    private final Date DATE_NOW = new Date(Instant.now().toEpochMilli());
    private final Date DATE_AFTER = new Date(Instant.now().toEpochMilli() + 1000000000);
    private final String RANDOM_FIRST_NAME = "FirstName";
    private final String RANDOM_LAST_NAME = "LastName";
    private final String RANDOM_EMAIL = "email@email.com";
    private final String RANDOM_PASSWORD = "RandomPassword";
    private final String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    private final String RANDOM_ROLE_NAME = "ROLE_RANDOM";
    private final LocalDate LOCAL_DATE_NOW = LocalDate.now();
    private final String RANDOM_COUNTRY_NAME = "Poland";
    private final String RANDOM_PROVINCE_NAME = "Mazowieckie";
    private final String RANDOM_CITY = "Warsaw";
    private final String RANDOM_ADDRESS = "XYZ 17/A";
    private final String RANDOM_DELIVERY_PROVIDER_NAME = "Credit card";
    private final boolean RANDOM_ENABLED_VALUE = true;
    private final Integer RANDOM_HEIGHT = 100;
    private final Integer RANDOM_WIDTH = 100;
    private ReturnCause RANDOM_RETURN_CAUSE = ReturnCause.DAMAGED;
    private ReturnCause DIFFERENT_RETURN_CAUSE = ReturnCause.LOW_QUALITY;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DeliveryProviderRepository deliveryProviderRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReturnedProductRepository returnedProductRepository;

    @Autowired
    private ReturnTransactionRepository returnTransactionRepository;

    private Product product1;
    private Product product2;
    private Address address;
    private DeliveryProvider deliveryProvider;
    private Privilege privilege;
    private Role role;
    private User user;
    private ReturnedProduct returnedProduct1;
    private ReturnedProduct returnedProduct2;
    private ReturnTransaction returnTransaction1;
    private ReturnTransaction returnTransaction2;
    private final UUID RANDOM_ORDER_TRANSACTION_ID = UUID.randomUUID();

    @BeforeEach
    public void setUp() {

        product1 = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_HEIGHT, RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, RANDOM_STOCK, RANDOM_PRODUCT_MAIN_IMAGE);
        productRepository.save(product1);

        product2 = new Product(DIFFERENT_PRODUCT_NAME, DIFFERENT_EAN_CODE, DIFFERENT_TYPE_LOWER_CASE, RANDOM_DESCRIPTION,
                RANDOM_HEIGHT, RANDOM_WIDTH, RANDOM_REGULAR_PRICE, RANDOM_CURRENT_PRICE, DIFFERENT_STOCK, DIFFERENT_PRODUCT_MAIN_IMAGE);
        productRepository.save(product2);

        privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        user = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, LOCAL_DATE_NOW,
                role);
        userRepository.save(user);

        address = new Address(RANDOM_COUNTRY_NAME, RANDOM_PROVINCE_NAME, RANDOM_CITY, RANDOM_ADDRESS);
        addressRepository.save(address);

        deliveryProvider = new DeliveryProvider(RANDOM_DELIVERY_PROVIDER_NAME, RANDOM_ENABLED_VALUE);
        deliveryProviderRepository.save(deliveryProvider);

        returnedProduct1 = new ReturnedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice(),
                RANDOM_ORDER_TRANSACTION_ID);
        returnedProductRepository.save(returnedProduct1);

        returnTransaction1 = new ReturnTransaction(DATE_NOW, user, address, deliveryProvider, RANDOM_RETURN_CAUSE,
                List.of(returnedProduct1));
        returnTransactionRepository.save(returnTransaction1);

        returnedProduct1.setReturnTransaction(returnTransaction1);

        returnedProduct2 = new ReturnedProduct(product2, DIFFERENT_QUANTITY, product2.getCurrentPrice(),
                RANDOM_ORDER_TRANSACTION_ID);
        returnedProductRepository.save(returnedProduct2);

        returnTransaction2 = new ReturnTransaction(DATE_NOW, user, address, deliveryProvider, DIFFERENT_RETURN_CAUSE,
                List.of(returnedProduct2));
        returnTransactionRepository.save(returnTransaction2);

        returnedProduct2.setReturnTransaction(returnTransaction2);
    }

    @Test
    public void testOfSave(){

        ReturnedProduct returnedProduct = new ReturnedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice(),
                RANDOM_ORDER_TRANSACTION_ID);

        assertDoesNotThrow(() -> returnedProductRepository.save(returnedProduct));
    }

    @Test
    public void testOfGetAllQuantityOfReturnedProductsAndRevenue(){

        List<Object[]> quantityAndRevenue =  returnedProductRepository.getAllQuantityOfReturnedProductsAndRevenue();

        Long quantity = (Long) quantityAndRevenue.get(0)[0];
        Double revenue = (Double) quantityAndRevenue.get(0)[1];

        assertEquals(quantity, RANDOM_QUANTITY + DIFFERENT_QUANTITY);
        assertEquals(revenue, RANDOM_QUANTITY * product1.getCurrentPrice() + DIFFERENT_QUANTITY * product2.getCurrentPrice());
    }

    @Test
    public void testOfGetAllTypesAndTheirReturnedQuantityAndRevenue(){

        List<Object[]> list = returnedProductRepository.getAllTypesAndTheirReturnedQuantityAndRevenue();

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
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(){

        List<Object[]> list =returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);

        assertEquals(list.size(), 2);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByPhrase(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByPhrase(RANDOM_PHRASE_LOWER_CASE);

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
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByType(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByType(RANDOM_TYPE_LOWER_CASE);

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
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByUserEmail(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByUserEmail(RANDOM_EMAIL);

        assertEquals(list.size(), 2);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                        DATE_BEFORE, DATE_AFTER, RANDOM_PHRASE_LOWER_CASE);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                        DATE_BEFORE, DATE_AFTER, RANDOM_TYPE_LOWER_CASE);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                        DATE_BEFORE, DATE_AFTER, RANDOM_EMAIL);

        assertEquals(list.size(), 2);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByPhraseAndType(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndType(
                        RANDOM_PHRASE_LOWER_CASE, RANDOM_TYPE_LOWER_CASE);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByPhraseAndUserEmail(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndUserEmail(
                        RANDOM_PHRASE_LOWER_CASE, RANDOM_EMAIL);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTypeAndUserEmail(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTypeAndUserEmail(
                        RANDOM_TYPE_LOWER_CASE, RANDOM_EMAIL);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndType(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndType(
                        DATE_BEFORE, DATE_AFTER, RANDOM_PHRASE_LOWER_CASE, RANDOM_TYPE_LOWER_CASE);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndUserEmail(){

        List<Object[]> list = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndUserEmail(
                        DATE_BEFORE, DATE_AFTER, RANDOM_PHRASE_LOWER_CASE, RANDOM_EMAIL);

        assertEquals(list.size(), 1);
    }

    @Test
    public void testOfGetAllQuantityOfReturnedProductsAndRevenueByTimePeriod(){

        List<Object[]> result = returnedProductRepository.getAllQuantityOfReturnedProductsAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);

        Object[] quantityAndRevenue = result.get(0);

        Long quantity = (Long) quantityAndRevenue[0];
        Double revenue = (Double) quantityAndRevenue[1];

        assertEquals(result.size(), 1);
        assertEquals(quantity, RANDOM_QUANTITY + DIFFERENT_QUANTITY);
        assertEquals(revenue, RANDOM_QUANTITY * returnedProduct1.getPricePerUnit() + DIFFERENT_QUANTITY * product2.getCurrentPrice());
    }

    @Test
    public void testOfGetAllTypesAndTheirQuantityOfReturnedProductsAndRevenueByTimePeriod(){

        List<Object[]> result = returnedProductRepository
                .getAllTypesAndTheirQuantityOfReturnedProductsAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            map1.put((String) row[0], (Long) row[1]);
            map2.put((String) row[0], (Double) row[2]);
        });

        assertEquals(map1.size(), 2);
        assertEquals(map1.get(RANDOM_TYPE_LOWER_CASE), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_TYPE_LOWER_CASE), RANDOM_QUANTITY * returnedProduct1.getPricePerUnit());
        assertEquals(map1.get(DIFFERENT_TYPE_LOWER_CASE), DIFFERENT_QUANTITY);
        assertEquals(map2.get(DIFFERENT_TYPE_LOWER_CASE), DIFFERENT_QUANTITY * returnedProduct2.getPricePerUnit());
    }
}
