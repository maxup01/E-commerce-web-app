package org.example.backend.dao.repository.transaction;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
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
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class OrderTransactionRepositoryTest {

    private final String RANDOM_DELIVERY_PROVIDER_NAME = "Random delivery provider";
    private final String RANDOM_COUNTRY_NAME = "Random country";
    private final String RANDOM_PROVINCE_NAME = "Random province";
    private final String RANDOM_CITY_NAME = "Random city";
    private final String RANDOM_ADDRESS = "Random address";
    private final String RANDOM_PAYMENT_METHOD = "Random payment method";
    private final Long RANDOM_QUANTITY = 1L;
    private final String RANDOM_EAN_CODE = "32211421412";
    private final String RANDOM_PRODUCT_NAME = "Random product name";
    private final String RANDOM_TYPE = "Random type";
    private final String RANDOM_PRODUCT_DESCRIPTION = "Random product description";
    private final Double RANDOM_PRICE = 5.00;
    private final byte[] RANDOM_MAIN_IMAGE = new byte[10];
    private final byte[] DIFFERENT_IMAGE = new byte[12];
    private final Date TODAYS_DATE = Date.from(ZonedDateTime.now().toInstant());
    private final String RANDOM_FIRST_NAME = "FirstName";
    private final String RANDOM_LAST_NAME = "LastName";
    private final String RANDOM_EMAIL = "email@email.com";
    private final String RANDOM_PASSWORD = "RandomPassword";
    private final LocalDate RANDOM_DATE = LocalDate.of(1950, 1, 1);
    private final String RANDOM_PRIVILEGE_NAME = "RANDOM_PRIVILEGE";
    private final String RANDOM_ROLE_NAME = "ROLE_RANDOM";
    private final boolean RANDOM_ENABLED = true;
    private final Date DATE_BEFORE = new Date(0);
    private final Date DATE_AFTER = new Date(Instant.now().toEpochMilli() + 1000000000);
    private final Date DATE_NOT_IN_RANGE = new Date(Instant.now().toEpochMilli() + 100000000000L);

    @Autowired
    private OrderTransactionRepository orderTransactionRepository;

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DeliveryProviderRepository deliveryProviderRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private EntityManager entityManager;

    private Product product;
    private Address address;
    private DeliveryProvider deliveryProvider;
    private PaymentMethod paymentMethod;
    private Privilege privilege;
    private Role role;
    private User user;
    private OrderedProduct orderedProduct;
    private OrderTransaction orderTransaction;
    private OrderedProduct orderedProduct2;
    private OrderTransaction orderTransaction2;


    @BeforeEach
    public void setUp() {

        Stock stock = new Stock(RANDOM_QUANTITY);
        ProductMainImage productMainImage = new ProductMainImage(RANDOM_MAIN_IMAGE);
        ProductPageImage productPageImage = new ProductPageImage(DIFFERENT_IMAGE);
        product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_PRODUCT_DESCRIPTION,
                RANDOM_PRICE, RANDOM_PRICE, stock, productMainImage, List.of(productPageImage));
        productRepository.save(product);

        address = new Address(RANDOM_COUNTRY_NAME, RANDOM_PROVINCE_NAME, RANDOM_CITY_NAME, RANDOM_ADDRESS);
        addressRepository.save(address);

        deliveryProvider = new DeliveryProvider(RANDOM_DELIVERY_PROVIDER_NAME, RANDOM_ENABLED);
        deliveryProviderRepository.save(deliveryProvider);

        paymentMethod = new PaymentMethod(RANDOM_PAYMENT_METHOD, RANDOM_ENABLED);
        paymentMethodRepository.save(paymentMethod);

        privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        user = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE,
                role);
        userRepository.save(user);

        orderedProduct = new OrderedProduct(product, RANDOM_QUANTITY, RANDOM_PRICE);
        orderedProductRepository.save(orderedProduct);

        orderTransaction = new OrderTransaction(TODAYS_DATE, user, address,
                deliveryProvider, paymentMethod, List.of(orderedProduct));

        orderedProduct2 = new OrderedProduct(product, RANDOM_QUANTITY, RANDOM_PRICE);
        orderedProductRepository.save(orderedProduct2);

        orderTransaction2 = new OrderTransaction(DATE_NOT_IN_RANGE, user, address,
                deliveryProvider, paymentMethod, List.of(orderedProduct));
        orderTransactionRepository.save(orderTransaction2);
    }

    @Test
    public void testOfSave(){

        assertDoesNotThrow(() -> {
            orderTransactionRepository.save(orderTransaction);
            entityManager.flush();
        });
    }

    @Test
    public void testOfGetCountOfAllOrderTransactionsByTimePeriod(){

        orderTransactionRepository.save(orderTransaction);

        Long count = orderTransactionRepository.getCountOfAllOrderTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER);

        assertEquals(count, 1L);
    }

    @Test
    public void testOfFindProductsByTimePeriod(){

        orderTransactionRepository.save(orderTransaction);

        List<OrderTransaction> orders = orderTransactionRepository.findProductsByTimePeriod(DATE_BEFORE, DATE_AFTER);

        assertEquals(orders.size(), 1);
        assertEquals(orders.get(0).getDeliveryAddress(), address);
        assertEquals(orders.get(0).getPaymentMethod(), paymentMethod);
        assertEquals(orders.get(0).getTransactionDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindProductsByTimePeriodAndPaymentMethodName(){

        orderTransactionRepository.save(orderTransaction);

        List<OrderTransaction> orders = orderTransactionRepository.findProductsByTimePeriodAndPaymentMethodName(
                DATE_BEFORE, DATE_AFTER, RANDOM_PAYMENT_METHOD);

        assertEquals(orders.size(), 1);
        assertEquals(orders.get(0).getDeliveryAddress(), address);
        assertEquals(orders.get(0).getPaymentMethod(), paymentMethod);
        assertEquals(orders.get(0).getTransactionDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindProductsByTimePeriodAndDeliveryProviderName(){

        orderTransactionRepository.save(orderTransaction);

        List<OrderTransaction> orders = orderTransactionRepository.findProductsByTimePeriodAndDeliveryProviderName(
                DATE_BEFORE, DATE_AFTER, RANDOM_DELIVERY_PROVIDER_NAME);

        assertEquals(orders.size(), 1);
        assertEquals(orders.get(0).getDeliveryAddress(), address);
        assertEquals(orders.get(0).getPaymentMethod(), paymentMethod);
        assertEquals(orders.get(0).getTransactionDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindProductsByTimePeriodAndUserEmail(){

        orderTransactionRepository.save(orderTransaction);

        List<OrderTransaction> orders = orderTransactionRepository
                .findProductsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, RANDOM_EMAIL);

        assertEquals(orders.size(), 1);
        assertEquals(orders.get(0).getDeliveryAddress(), address);
        assertEquals(orders.get(0).getPaymentMethod(), paymentMethod);
        assertEquals(orders.get(0).getTransactionDate(), TODAYS_DATE);
    }
}
