package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.PaymentMethod;
import org.example.backend.dao.entity.user.Privilege;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.logistic.AddressRepository;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.dao.repository.user.PrivilegeRepository;
import org.example.backend.dao.repository.user.RoleRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    private final String RANDOM_PAYMENT_METHOD_NAME_LOWER_CASE = "random payment method name";
    private final Integer RANDOM_HEIGHT = 100;
    private final Integer RANDOM_WIDTH = 100;

    @Autowired
    private DeliveryProviderRepository deliveryProviderRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private OrderTransactionRepository orderTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private AddressRepository addressRepository;

    private Product product1;
    private Product product2;
    private Privilege privilege;
    private Role role;
    private User user;
    private Address address;
    private DeliveryProvider deliveryProvider;
    private PaymentMethod paymentMethod;
    private OrderedProduct orderedProduct1;
    private OrderedProduct orderedProduct2;
    private OrderTransaction orderTransaction1;
    private OrderTransaction orderTransaction2;

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

        paymentMethod = new PaymentMethod(RANDOM_PAYMENT_METHOD_NAME_LOWER_CASE, RANDOM_ENABLED_VALUE);
        paymentMethodRepository.save(paymentMethod);

        orderedProduct1 = new OrderedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice());
        orderedProductRepository.save(orderedProduct1);

        ArrayList<OrderedProduct> orderedProducts1 = new ArrayList<>();
        orderedProducts1.add(orderedProduct1);

        orderTransaction1 = new OrderTransaction(DATE_NOW, user, address, deliveryProvider, paymentMethod, orderedProducts1);
        orderTransactionRepository.save(orderTransaction1);

        orderedProduct1.setOrderTransaction(orderTransaction1);

        orderedProduct2 = new OrderedProduct(product2, DIFFERENT_QUANTITY, product2.getCurrentPrice());
        orderedProductRepository.save(orderedProduct2);

        ArrayList<OrderedProduct> orderedProducts2 = new ArrayList<>();
        orderedProducts2.add(orderedProduct2);

        orderTransaction2 = new OrderTransaction(DATE_NOW, user, address, deliveryProvider, paymentMethod, orderedProducts2);
        orderTransactionRepository.save(orderTransaction2);

        orderedProduct2.setOrderTransaction(orderTransaction2);
    }

    @Test
    public void testOfSave(){

        OrderedProduct orderedProduct = new OrderedProduct(product1, RANDOM_QUANTITY, product1.getCurrentPrice());

        assertDoesNotThrow(() -> orderedProductRepository.save(orderedProduct));
    }

    @Test
    public void testOfGetAllQuantityOfOrderedProductsAndRevenue(){

        List<Object[]> quantityAndRevenue =  orderedProductRepository.getAllQuantityOfOrderedProductsAndRevenue();

        Long quantity = (Long) quantityAndRevenue.get(0)[0];
        Double revenue = (Double) quantityAndRevenue.get(0)[1];

        assertEquals(quantity, RANDOM_QUANTITY + DIFFERENT_QUANTITY);
        assertEquals(revenue, RANDOM_QUANTITY * product1.getCurrentPrice() + DIFFERENT_QUANTITY * product2.getCurrentPrice());
    }

    @Test
    public void testOfGetAllTypesAndTheirOrderedQuantityAndRevenue(){

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

    @Test
    public void testOfGetAllQuantityOfOrderedProductsAndRevenueByTimePeriod(){

        List<Object[]> result = orderedProductRepository.getAllQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);

        Object[] quantityAndRevenue = result.get(0);

        Long quantity = (Long) quantityAndRevenue[0];
        Double revenue = (Double) quantityAndRevenue[1];

        assertEquals(result.size(), 1);
        assertEquals(quantity, RANDOM_QUANTITY + DIFFERENT_QUANTITY);
        assertEquals(revenue, RANDOM_QUANTITY * orderedProduct1.getPricePerUnit() + DIFFERENT_QUANTITY * product2.getCurrentPrice());
    }

    @Test
    public void testOfGetAllTypesAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriod(){

        List<Object[]> result = orderedProductRepository
                .getAllTypesAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            map1.put((String) row[0], (Long) row[1]);
            map2.put((String) row[0], (Double) row[2]);
        });

        assertEquals(map1.size(), 2);
        assertEquals(map1.get(RANDOM_TYPE_LOWER_CASE), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_TYPE_LOWER_CASE), RANDOM_QUANTITY * orderedProduct1.getPricePerUnit());
        assertEquals(map1.get(DIFFERENT_TYPE_LOWER_CASE), DIFFERENT_QUANTITY);
        assertEquals(map2.get(DIFFERENT_TYPE_LOWER_CASE), DIFFERENT_QUANTITY * orderedProduct2.getPricePerUnit());
    }

    @Test
    public void testOfGetAllTypesAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndPhrase(){

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndPhrase(DATE_BEFORE, DATE_AFTER, RANDOM_PHRASE_LOWER_CASE);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            Product pomProduct = (Product) row[0];
            map1.put(pomProduct.getName(), (Long) row[1]);
            map2.put(pomProduct.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 1);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * orderedProduct1.getPricePerUnit());
    }

    @Test
    public void testOfGetProductsAndTheirOrderedQuantityAndRevenueByTimePeriod(){

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirOrderedQuantityAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            Product product = (Product) row[0];
            map1.put(product.getName(), (Long) row[1]);
            map2.put(product.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 2);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * orderedProduct1.getPricePerUnit());
        assertEquals(map1.get(DIFFERENT_PRODUCT_NAME), DIFFERENT_QUANTITY);
        assertEquals(map2.get(DIFFERENT_PRODUCT_NAME), DIFFERENT_QUANTITY * orderedProduct2.getPricePerUnit());
    }

    @Test
    public void testOfGetProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndType(){

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndType(
                        DATE_BEFORE, DATE_AFTER, RANDOM_TYPE_LOWER_CASE);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            Product product = (Product) row[0];
            map1.put(product.getName(), (Long) row[1]);
            map2.put(product.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 1);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * orderedProduct1.getPricePerUnit());
    }

    @Test
    public void testOfGetProductsAndTheirQuantityOfOrderedProductsAndRevenueByTypeAndPhrase(){

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTypeAndPhrase(
                        RANDOM_TYPE_LOWER_CASE, RANDOM_PHRASE_LOWER_CASE);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            Product product = (Product) row[0];
            map1.put(product.getName(), (Long) row[1]);
            map2.put(product.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 1);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * orderedProduct1.getPricePerUnit());
    }

    @Test
    public void testOfGetProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndTypeAndPhrase(){

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndTypeAndPhrase(
                        DATE_BEFORE, DATE_AFTER, RANDOM_TYPE_LOWER_CASE, RANDOM_PHRASE_LOWER_CASE);

        HashMap<String, Long> map1 = new HashMap<>();
        HashMap<String, Double> map2 = new HashMap<>();

        result.forEach(row -> {
            Product product = (Product) row[0];
            map1.put(product.getName(), (Long) row[1]);
            map2.put(product.getName(), (Double) row[2]);
        });

        assertEquals(map1.size(), 1);
        assertEquals(map1.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY);
        assertEquals(map2.get(RANDOM_PRODUCT_NAME), RANDOM_QUANTITY * orderedProduct1.getPricePerUnit());
    }
}
