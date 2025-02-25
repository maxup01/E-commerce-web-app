package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
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
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReturnTransactionRepositoryTest {

    private final String RANDOM_DELIVERY_PROVIDER_NAME = "Random delivery provider";
    private final String RANDOM_COUNTRY_NAME = "Random country";
    private final String RANDOM_PROVINCE_NAME = "Random province";
    private final String RANDOM_CITY_NAME = "Random city";
    private final String RANDOM_ADDRESS = "Random address";
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
    private final String RANDOM_CAUSE_NAME_LOWER_CASE = "random cause";
    private final Integer RANDOM_HEIGHT = 100;
    private final Integer RANDOM_WIDTH = 100;
    private ReturnCause RANDOM_RETURN_CAUSE = ReturnCause.MISLEADING_DATA;
    private final UUID RANDOM_ORDER_TRANSACTION_ID = UUID.randomUUID();

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

    private Product product;
    private Address address;
    private DeliveryProvider deliveryProvider;
    private Privilege privilege;
    private Role role;
    private User user;
    private ReturnedProduct returnedProduct;
    private ReturnTransaction returnTransaction;
    private ReturnedProduct returnedProduct2;
    private ReturnTransaction returnTransaction2;

    @BeforeEach
    public void setUp() {

        Stock stock = new Stock(RANDOM_QUANTITY);
        ProductMainImage productMainImage = new ProductMainImage(RANDOM_MAIN_IMAGE);
        ProductPageImage productPageImage = new ProductPageImage(DIFFERENT_IMAGE);
        product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_PRODUCT_DESCRIPTION, RANDOM_HEIGHT
                , RANDOM_WIDTH, RANDOM_PRICE, RANDOM_PRICE, stock, productMainImage, List.of(productPageImage));
        productRepository.save(product);

        address = new Address(RANDOM_COUNTRY_NAME, RANDOM_PROVINCE_NAME, RANDOM_CITY_NAME, RANDOM_ADDRESS);
        addressRepository.save(address);

        deliveryProvider = new DeliveryProvider(RANDOM_DELIVERY_PROVIDER_NAME, RANDOM_ENABLED);
        deliveryProviderRepository.save(deliveryProvider);

        privilege = new Privilege(RANDOM_PRIVILEGE_NAME);
        privilegeRepository.save(privilege);

        role = new Role(RANDOM_ROLE_NAME, List.of(privilege));
        roleRepository.save(role);

        user = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD, RANDOM_DATE,
                role);
        userRepository.save(user);

        returnedProduct = new ReturnedProduct(product, RANDOM_QUANTITY, RANDOM_PRICE, RANDOM_ORDER_TRANSACTION_ID);
        returnedProductRepository.save(returnedProduct);

        returnTransaction = new ReturnTransaction(TODAYS_DATE, user, address,
                deliveryProvider, RANDOM_RETURN_CAUSE, List.of(returnedProduct));

        returnedProduct2 = new ReturnedProduct(product, RANDOM_QUANTITY, RANDOM_PRICE, RANDOM_ORDER_TRANSACTION_ID);
        returnedProductRepository.save(returnedProduct2);

        returnTransaction2 = new ReturnTransaction(DATE_NOT_IN_RANGE, user, address,
                deliveryProvider, RANDOM_RETURN_CAUSE, List.of(returnedProduct));
        returnTransactionRepository.save(returnTransaction2);
    }

    @Test
    public void testOfSave(){

        assertDoesNotThrow(() -> {
            returnTransactionRepository.save(returnTransaction);
        });
    }

    @Test
    public void testOfGetCountOfAllReturnTransactionsByTimePeriod(){

        returnTransactionRepository.save(returnTransaction);

        Long count = returnTransactionRepository.getCountOfAllReturnTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER);

        assertEquals(count, 1L);
    }

    @Test
    public void testOfFindReturnTransactionsByTimePeriod(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER);

        assertEquals(returns.size(), 1);
        assertEquals(returns.get(0).getDeliveryAddress(), address);
        assertEquals(returns.get(0).getReturnCause(), RANDOM_RETURN_CAUSE);
        assertEquals(returns.get(0).getDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindReturnTransactionsByReturnCause(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByReturnCause(RANDOM_RETURN_CAUSE);

        assertEquals(returns.size(), 2);
        assertEquals(returns.get(0).getDeliveryAddress(), address);
        assertEquals(returns.get(0).getReturnCause(), RANDOM_RETURN_CAUSE);
        assertTrue((returns.get(0).getDate() == TODAYS_DATE)
                || (returns.get(0).getDate() == DATE_NOT_IN_RANGE));
    }

    @Test
    public void testOfFindReturnTransactionsByDeliveryProviderName(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByDeliveryProviderName(RANDOM_DELIVERY_PROVIDER_NAME);

        assertEquals(returns.size(), 2);
    }

    @Test
    public void testOfFindReturnTransactionsByUserEmail(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByUserEmail(RANDOM_EMAIL);

        assertEquals(returns.size(), 2);
    }

    @Test
    public void testOfFindReturnTransactionsByTimePeriodAndReturnCause(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseName(DATE_BEFORE, DATE_AFTER, RANDOM_RETURN_CAUSE);

        assertEquals(returns.size(), 1);
        assertEquals(returns.get(0).getDeliveryAddress(), address);
        assertEquals(returns.get(0).getReturnCause(), RANDOM_RETURN_CAUSE);
        assertEquals(returns.get(0).getDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindReturnTransactionsByTimePeriodAndDeliveryProviderName(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndDeliveryProviderName(DATE_BEFORE, DATE_AFTER,
                        RANDOM_DELIVERY_PROVIDER_NAME);

        assertEquals(returns.size(), 1);
        assertEquals(returns.get(0).getDeliveryAddress(), address);
        assertEquals(returns.get(0).getReturnCause(), RANDOM_RETURN_CAUSE);
        assertEquals(returns.get(0).getDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindReturnTransactionsByTimePeriodAndUserEmail(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, RANDOM_EMAIL);

        assertEquals(returns.size(), 1);
        assertEquals(returns.get(0).getDeliveryAddress(), address);
        assertEquals(returns.get(0).getReturnCause(), RANDOM_RETURN_CAUSE);
        assertEquals(returns.get(0).getDate(), TODAYS_DATE);
    }

    @Test
    public void testOfFindReturnTransactionsByReturnCauseAndDeliveryProviderName(){

        returnTransactionRepository.save(returnTransaction);

        List<ReturnTransaction> returns = returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndDeliveryProviderName(
                        RANDOM_RETURN_CAUSE, RANDOM_DELIVERY_PROVIDER_NAME);

        assertEquals(returns.size(), 2);
    }
}
