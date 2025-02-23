package org.example.backend.dao.service;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.UserImage;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.PaymentMethod;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.logistic.AddressRepository;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.dao.repository.transaction.OrderTransactionRepository;
import org.example.backend.dao.repository.transaction.OrderedProductRepository;
import org.example.backend.dao.repository.transaction.PaymentMethodRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.enumerated.TransactionStatus;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.exception.transaction.PaymentMethodNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.AddressModel;
import org.example.backend.model.OrderTransactionModel;
import org.example.backend.model.OrderedProductModel;
import org.example.backend.model.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderTransactionServiceTest {

    private final UUID ID_OF_ORDER_TRANSACTION_THAT_EXIST = UUID.randomUUID();
    private final UUID ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST = UUID.randomUUID();
    private final String RANDOM_EMAIL = "random@email.com";
    private final String WRONG_EMAIL = "wrongemailcom";
    private final String EMAIL_OF_USER_THAT_NOT_EXIST = "not@exist.com";
    private final String NAME_OF_DELIVERY_PROVIDER_THAT_EXIST = "Delivery provider name";
    private final String NAME_OF_DELIVERY_PROVIDER_THAT_NOT_EXIST = "Not exist";
    private final String NAME_OF_PAYMENT_METHOD_THAT_EXIST = "Payment method name";
    private final String NAME_OF_PAYMENT_METHOD_THAT_NOT_EXIST = "Different dhaidada";
    private final String COUNTRY_NAME = "country name";
    private final String PROVINCE_NAME = "province name";
    private final String CITY_NAME = "city name";
    private final String ADDRESS = "random address";
    private final UUID ID_OF_PRODUCT_THAT_EXIST = UUID.randomUUID();
    private final UUID ID_OF_PRODUCT_THAT_NOT_EXIST = UUID.randomUUID();
    private final String RANDOM_EAN_CODE = "73920483";
    private final String RANDOM_PRODUCT_NAME = "random product name";
    private final String RANDOM_PRODUCT_TYPE = "random type";
    private final String RANDOM_PRODUCT_DESCRIPTION = "random description";
    private final Double RANDOM_PRODUCT_PRICE = 15.00;
    private final Double GREATER_PRODUCT_PRICE = 15.00;
    private final Integer RANDOM_PRODUCT_HEIGHT = 100;
    private final Integer RANDOM_PRODUCT_WIDTH = 80;
    private final byte[] RANDOM_IMAGE = new byte[12];
    private final Long RANDOM_QUANTITY = 30L;
    private final Long ORDERED_QUANTITY = 7L;
    private final Date DATE_BEFORE = new Date(0);
    private final Date DATE_NOW = Date.from(Instant.now());
    private final Date DATE_AFTER = new Date(10000000000000000L);
    private final String RANDOM_PAYMENT_NAME = "random payment name";
    private final String DELIVERY_PROVIDER_NAME = "delivery provider name";
    private final String RANDOM_PHRASE = "random phrase";
    private final TransactionStatus RANDOM_STATUS = TransactionStatus.DELIVERED;
    private final TransactionStatus WRONG_STATUS_FOR_ORDER_TRANSACTION = TransactionStatus.ACCEPTED_RETURN;
    private final String RANDOM_FIRST_NAME = "random first name";
    private final String RANDOM_LAST_NAME = "random last name";
    private final String RANDOM_PASSWORD = "random password";
    private final Role RANDOM_ROLE = new Role();
    private final UserImage USER_IMAGE = new UserImage(new byte[12]);
    private final LocalDate BIRTH_DATE = LocalDate.of(2020, 1, 1);
    private final Double RANDOM_PRICE = 15.00;


    @Mock
    PaymentMethodRepository paymentMethodRepository;

    @Mock
    DeliveryProviderRepository deliveryProviderRepository;

    @Mock
    AddressRepository addressRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderedProductRepository orderedProductRepository;

    @Mock
    OrderTransactionRepository orderTransactionRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    OrderTransactionService orderTransactionService;

    private OrderTransaction orderTransaction;
    private Product product;
    private User user;
    private OrderedProduct orderedProduct;

    @BeforeEach
    public void setUp() {

        product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_PRODUCT_TYPE, RANDOM_PRODUCT_DESCRIPTION,
                RANDOM_PRODUCT_HEIGHT, RANDOM_PRODUCT_WIDTH, GREATER_PRODUCT_PRICE,
                RANDOM_PRODUCT_PRICE, new Stock(RANDOM_QUANTITY), new ProductMainImage(RANDOM_IMAGE));


        user = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD,
                BIRTH_DATE, RANDOM_ROLE, USER_IMAGE);

        orderTransaction = OrderTransaction
                .builder()
                .user(user)
                .deliveryAddress(new Address(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS))
                .deliveryProvider(new DeliveryProvider(DELIVERY_PROVIDER_NAME, true))
                .orderedProducts(new ArrayList<>())
                .paymentMethod(new PaymentMethod(RANDOM_PAYMENT_NAME, true))
                .build();

        orderedProduct = new OrderedProduct(product, ORDERED_QUANTITY, RANDOM_PRICE);
    }

    @Test
    public void testOfSaveNewOrderTransaction(){

        ProductModel productModel = new ProductModel();
        productModel.setId(ID_OF_PRODUCT_THAT_EXIST);

        OrderedProductModel orderedProductModel = OrderedProductModel
                .builder()
                .quantity(RANDOM_QUANTITY)
                .product(productModel)
                .build();

        ArrayList<OrderedProductModel> products = new ArrayList<>();
        products.add(orderedProductModel);

        when(productRepository.findById(ID_OF_PRODUCT_THAT_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_THAT_NOT_EXIST)).thenReturn(Optional.empty());

        when(userRepository.findByEmail(RANDOM_EMAIL)).thenReturn(user);
        when(userRepository.findByEmail(EMAIL_OF_USER_THAT_NOT_EXIST)).thenReturn(null);

        when(deliveryProviderRepository.findByName(NAME_OF_DELIVERY_PROVIDER_THAT_EXIST))
                .thenReturn(new DeliveryProvider());
        when(deliveryProviderRepository.findByName(NAME_OF_DELIVERY_PROVIDER_THAT_NOT_EXIST)).thenReturn(null);

        when(paymentMethodRepository.findByName(NAME_OF_PAYMENT_METHOD_THAT_EXIST)).thenReturn(new PaymentMethod());
        when(paymentMethodRepository.findByName(NAME_OF_PAYMENT_METHOD_THAT_NOT_EXIST)).thenReturn(null);

        when(orderedProductRepository.save(new OrderedProduct(product, ORDERED_QUANTITY, product.getCurrentPrice())))
                .thenReturn(new OrderedProduct(product, ORDERED_QUANTITY, product.getCurrentPrice()));

        when(orderTransactionRepository.save(any(OrderTransaction.class))).thenReturn(orderTransaction);
        when(orderedProductRepository.saveAll(anyList())).thenReturn(List.of(orderedProduct));
        when(addressRepository.save(any(Address.class))).thenReturn(orderTransaction.getDeliveryAddress());

        AddressModel addressModel = new AddressModel(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS);
        OrderTransactionModel orderTransactionModel = OrderTransactionModel
                .builder()
                .address(addressModel)
                .userEmail(RANDOM_EMAIL)
                .deliveryProviderName(NAME_OF_DELIVERY_PROVIDER_THAT_EXIST)
                .paymentMethodName(NAME_OF_PAYMENT_METHOD_THAT_EXIST)
                .orderedProducts(products)
                .build();

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(null);
        });

        orderTransactionModel.setUserEmail(null);

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setUserEmail(WRONG_EMAIL);

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setUserEmail(RANDOM_EMAIL);
        orderTransactionModel.setDeliveryProviderName(null);

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setDeliveryProviderName("");

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setDeliveryProviderName(NAME_OF_DELIVERY_PROVIDER_THAT_EXIST);
        orderTransactionModel.setPaymentMethodName(null);

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setPaymentMethodName("");

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setPaymentMethodName(NAME_OF_PAYMENT_METHOD_THAT_EXIST);
        orderTransactionModel.getAddress().setCountry(null);

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setCountry("");

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setCountry(COUNTRY_NAME);
        orderTransactionModel.getAddress().setProvince(null);

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setProvince("");

        Exception eleventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setProvince(PROVINCE_NAME);
        orderTransactionModel.getAddress().setCity(null);

        Exception twelthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setCity("");

        Exception thirteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setCity(CITY_NAME);
        orderTransactionModel.getAddress().setAddress(null);

        Exception fourteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setAddress("");

        Exception fifteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddress().setAddress(ADDRESS);
        orderTransactionModel.setOrderedProducts(null);

        Exception sixteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setOrderedProducts(new ArrayList<>());

        Exception seventeenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setOrderedProducts(products);

        OrderedProductModel pom = OrderedProductModel
                .builder()
                .product(null)
                .quantity(null)
                .build();

        ArrayList<OrderedProductModel> orderedProducts = new ArrayList<>();
        orderedProducts.add(pom);

        orderTransactionModel.setOrderedProducts(orderedProducts);

        Exception eighteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        ProductModel productModel2 = new ProductModel();
        productModel2.setId(ID_OF_PRODUCT_THAT_NOT_EXIST);

        OrderedProductModel orderedProductModel2 = OrderedProductModel
                .builder()
                .product(productModel2)
                .quantity(ORDERED_QUANTITY)
                .build();

        ArrayList<OrderedProductModel> orderedProducts2 = new ArrayList<>();
        orderedProducts2.add(orderedProductModel2);
        orderTransactionModel.setOrderedProducts(orderedProducts2);

        Exception nineteenthException = assertThrows(ProductNotFoundException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setOrderedProducts(products);
        orderTransactionModel.setUserEmail(EMAIL_OF_USER_THAT_NOT_EXIST);

        Exception twentiethException = assertThrows(UserNotFoundException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setUserEmail(RANDOM_EMAIL);
        orderTransactionModel.setDeliveryProviderName(NAME_OF_DELIVERY_PROVIDER_THAT_NOT_EXIST);

        Exception twentyFirstException = assertThrows(DeliveryProviderNotFoundException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setDeliveryProviderName(NAME_OF_DELIVERY_PROVIDER_THAT_EXIST);
        orderTransactionModel.setPaymentMethodName(NAME_OF_PAYMENT_METHOD_THAT_NOT_EXIST);

        Exception twentySecondException = assertThrows(PaymentMethodNotFoundException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setPaymentMethodName(NAME_OF_PAYMENT_METHOD_THAT_EXIST);

        OrderedProductModel orderedProductModelPom = OrderedProductModel
                .builder()
                .product(productModel)
                .quantity(RANDOM_QUANTITY + 10L)
                .build();

        ArrayList<OrderedProductModel> orderedProducts3 = new ArrayList<>();
        orderedProducts3.add(orderedProductModelPom);
        orderTransactionModel.setOrderedProducts(orderedProducts3);

        Exception twentyThirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setOrderedProducts(products);

        orderTransaction.setOrderedProducts(new ArrayList<>());

        assertDoesNotThrow(() -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        assertEquals(firstException.getMessage(), "Null argument: orderTransactionModel");
        assertEquals(secondException.getMessage(), "Incorrect argument field: orderTransactionModel.userEmail");
        assertEquals(thirdException.getMessage(), "Incorrect argument field: orderTransactionModel.userEmail");
        assertEquals(fourthException.getMessage(), "Incorrect argument field: orderTransactionModel.deliveryProviderName");
        assertEquals(fifthException.getMessage(), "Incorrect argument field: orderTransactionModel.deliveryProviderName");
        assertEquals(sixthException.getMessage(), "Incorrect argument field: orderTransactionModel.paymentMethodName");
        assertEquals(seventhException.getMessage(), "Incorrect argument field: orderTransactionModel.paymentMethodName");
        assertEquals(eighthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(ninthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(tenthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(eleventhException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(twelthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(thirteenthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(fourteenthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(fifteenthException.getMessage(), "Incorrect argument field: orderTransactionModel.addressModel");
        assertEquals(sixteenthException.getMessage(), "Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");
        assertEquals(seventeenthException.getMessage(), "Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");
        assertEquals(eighteenthException.getMessage(), "Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");
        assertEquals(nineteenthException.getMessage(), "Product with id " + ID_OF_PRODUCT_THAT_NOT_EXIST + " not found");
        assertEquals(twentiethException.getMessage(), "User with email " + EMAIL_OF_USER_THAT_NOT_EXIST + " not found");
        assertEquals(twentyFirstException.getMessage(), "Delivery Provider with name " + NAME_OF_DELIVERY_PROVIDER_THAT_NOT_EXIST + " not found");
        assertEquals(twentySecondException.getMessage(), "Payment Method with name " + NAME_OF_PAYMENT_METHOD_THAT_NOT_EXIST + " not found");
        assertEquals(twentyThirdException.getMessage(), "There is not enough stock for product with id " + ID_OF_PRODUCT_THAT_EXIST);
        assertEquals(product.getStock().getQuantity(), RANDOM_QUANTITY - ORDERED_QUANTITY);
    }

    @Test
    public void testOfUpdateOrderTransactionStatusById(){

        when(orderTransactionRepository.findById(ID_OF_ORDER_TRANSACTION_THAT_EXIST))
                .thenReturn(Optional.of(orderTransaction));
        when(orderTransactionRepository.findById(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST))
                .thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.updateOrderTransactionStatusById(null, RANDOM_STATUS);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.updateOrderTransactionStatusById(ID_OF_ORDER_TRANSACTION_THAT_EXIST, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.updateOrderTransactionStatusById(ID_OF_ORDER_TRANSACTION_THAT_EXIST,
                    WRONG_STATUS_FOR_ORDER_TRANSACTION);
        });

        Exception fourthException = assertThrows(OrderTransactionNotFoundException.class, () -> {
            orderTransactionService.updateOrderTransactionStatusById(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST,
                    RANDOM_STATUS);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.updateOrderTransactionStatusById(ID_OF_ORDER_TRANSACTION_THAT_EXIST,
                    RANDOM_STATUS);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: status");
        assertEquals(thirdException.getMessage(), "Incorrect argument: status");
        assertEquals(fourthException.getMessage(), "Order transaction with id " + ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetOrderTransactionById(){

        when(orderTransactionRepository.findById(ID_OF_ORDER_TRANSACTION_THAT_EXIST))
                .thenReturn(Optional.ofNullable(orderTransaction));
        when(orderTransactionRepository.findById(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST))
                .thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionById(null);
        });

        Exception secondException = assertThrows(OrderTransactionNotFoundException.class, () -> {
            orderTransactionService.getOrderTransactionById(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getOrderTransactionById(ID_OF_ORDER_TRANSACTION_THAT_EXIST);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Order transaction with id " + ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetCountOfAllOrderTransactionsByTimePeriod(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getCountOfAllOrderTransactionsByTimePeriod(null, DATE_NOW);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getCountOfAllOrderTransactionsByTimePeriod(DATE_AFTER, DATE_NOW);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getCountOfAllOrderTransactionsByTimePeriod(DATE_BEFORE, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getCountOfAllOrderTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getCountOfAllOrderTransactionsByTimePeriod(DATE_NOW, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getCountOfAllOrderTransactionsByTimePeriod(DATE_BEFORE, DATE_NOW);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetOrderTransactionsByTimePeriod(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriod(null, DATE_NOW);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriod(DATE_AFTER, DATE_NOW);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriod(DATE_BEFORE, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriod(DATE_NOW, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getOrderTransactionsByTimePeriod(DATE_BEFORE, DATE_NOW);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetOrderTransactionsByPaymentMethodName(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByPaymentMethodName(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByPaymentMethodName("");
        });

        assertDoesNotThrow(() -> {
            orderTransactionService
                    .getOrderTransactionsByPaymentMethodName(RANDOM_PAYMENT_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: paymentMethodName");
        assertEquals(secondException.getMessage(), "Incorrect argument: paymentMethodName");
    }

    @Test
    public void testOfGetOrderTransactionsByDeliveryProviderName(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByDeliveryProviderName(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByDeliveryProviderName("");
        });

        assertDoesNotThrow(() -> {
            orderTransactionService
                    .getOrderTransactionsByDeliveryProviderName(DELIVERY_PROVIDER_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(secondException.getMessage(), "Incorrect argument: deliveryProviderName");
    }

    @Test
    public void testOfGetOrderTransactionsByTimePeriodAndPaymentMethodName(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(null, DATE_NOW, RANDOM_PAYMENT_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_AFTER, DATE_NOW, RANDOM_PAYMENT_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_BEFORE, null, RANDOM_PAYMENT_NAME);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_BEFORE, DATE_AFTER, RANDOM_PAYMENT_NAME);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_NOW, DATE_BEFORE, RANDOM_PAYMENT_NAME);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_BEFORE, DATE_NOW, null);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_BEFORE, DATE_NOW, "");
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodName(DATE_BEFORE, DATE_NOW, RANDOM_PAYMENT_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(sixthException.getMessage(), "Incorrect argument: paymentMethodName");
        assertEquals(seventhException.getMessage(), "Incorrect argument: paymentMethodName");
    }

    @Test
    public void testOfGetOrderTransactionsByTimePeriodAndDeliveryProviderName(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(null, DATE_NOW,
                    DELIVERY_PROVIDER_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_AFTER, DATE_NOW,
                    DELIVERY_PROVIDER_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_BEFORE, null,
                    DELIVERY_PROVIDER_NAME);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_BEFORE, DATE_AFTER,
                    DELIVERY_PROVIDER_NAME);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_NOW, DATE_BEFORE,
                    DELIVERY_PROVIDER_NAME);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_BEFORE, DATE_NOW, null);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_BEFORE, DATE_NOW, "");
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndDeliveryProviderName(DATE_BEFORE, DATE_NOW,
                    DELIVERY_PROVIDER_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(sixthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(seventhException.getMessage(), "Incorrect argument: deliveryProviderName");
    }

    @Test
    public void testOfGetOrderTransactionsByTimePeriodAndUserEmail(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(null, DATE_NOW, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_AFTER, DATE_NOW, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, null, RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, RANDOM_EMAIL);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_NOW, DATE_BEFORE, RANDOM_EMAIL);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_NOW, null);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_NOW, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getOrderTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_NOW, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(sixthException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(seventhException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetProductsAndTheirOrderedQuantityAndRevenueByPhrase(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByPhrase(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByPhrase("");
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByPhrase(RANDOM_PHRASE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: phrase");
        assertEquals(secondException.getMessage(), "Incorrect argument: phrase");
    }

    @Test
    public void testOfGetProductsAndTheirOrderedQuantityAndRevenueByType(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByType(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByType(null);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByType(RANDOM_PRODUCT_TYPE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: type");
        assertEquals(secondException.getMessage(), "Incorrect argument: type");
    }

    @Test
    public void testOfGetQuantityOfOrderedProductsAndRevenueByTimePeriod(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getQuantityOfOrderedProductsAndRevenueByTimePeriod(null, DATE_NOW);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_AFTER, DATE_NOW);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_BEFORE, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_NOW, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getQuantityOfOrderedProductsAndRevenueByTimePeriod(DATE_BEFORE, DATE_NOW);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(null,
                    DATE_NOW);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(DATE_AFTER,
                    DATE_NOW);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(DATE_BEFORE,
                    null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(DATE_BEFORE,
                    DATE_AFTER);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(DATE_NOW,
                    DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(DATE_BEFORE,
                    DATE_NOW);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    null, DATE_NOW, RANDOM_PHRASE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_AFTER, DATE_NOW, RANDOM_PHRASE);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_BEFORE, null, RANDOM_PHRASE);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_BEFORE, DATE_AFTER, RANDOM_PHRASE);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_NOW, DATE_BEFORE, RANDOM_PHRASE);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_BEFORE, DATE_NOW, null);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_BEFORE, DATE_NOW, ""
            );
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                    DATE_BEFORE, DATE_NOW, RANDOM_PHRASE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(thirdException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(fifthException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(sixthException.getMessage(), "Incorrect argument: phrase");
        assertEquals(seventhException.getMessage(), "Incorrect argument: phrase");
    }
}
