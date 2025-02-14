package org.example.backend.dao.service;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.PaymentMethod;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.logistic.AddressRepository;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.dao.repository.transaction.OrderTransactionRepository;
import org.example.backend.dao.repository.transaction.OrderedProductRepository;
import org.example.backend.dao.repository.transaction.PaymentMethodRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.exception.transaction.PaymentMethodNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.AddressModel;
import org.example.backend.model.OrderTransactionModel;
import org.example.backend.model.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    public void setUp() {

        orderTransaction = new OrderTransaction();
        product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_PRODUCT_TYPE, RANDOM_PRODUCT_DESCRIPTION,
                RANDOM_PRODUCT_HEIGHT, RANDOM_PRODUCT_WIDTH, GREATER_PRODUCT_PRICE,
                RANDOM_PRODUCT_PRICE, new Stock(RANDOM_QUANTITY), new ProductMainImage(RANDOM_IMAGE));
        user = new User();
        user.setEmail(RANDOM_EMAIL);
    }

    @Test
    public void testOfSaveNewOrderTransaction(){

        ProductModel productModel = new ProductModel();
        productModel.setId(ID_OF_PRODUCT_THAT_EXIST);

        HashMap<ProductModel, Long> products = new HashMap<>();
        products.put(productModel, ORDERED_QUANTITY);

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

        AddressModel addressModel = new AddressModel(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS);
        OrderTransactionModel orderTransactionModel = OrderTransactionModel
                .builder()
                .addressModel(addressModel)
                .userEmail(RANDOM_EMAIL)
                .deliveryProviderName(NAME_OF_DELIVERY_PROVIDER_THAT_EXIST)
                .paymentMethodName(NAME_OF_PAYMENT_METHOD_THAT_EXIST)
                .productsAndOrderedQuantity(products)
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
        orderTransactionModel.getAddressModel().setCountry(null);

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setCountry("");

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setCountry(COUNTRY_NAME);
        orderTransactionModel.getAddressModel().setProvince(null);

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setProvince("");

        Exception eleventhException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setProvince(PROVINCE_NAME);
        orderTransactionModel.getAddressModel().setCity(null);

        Exception twelthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setCity("");

        Exception thirteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setCity(CITY_NAME);
        orderTransactionModel.getAddressModel().setAddress(null);

        Exception fourteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setAddress("");

        Exception fifteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.getAddressModel().setAddress(ADDRESS);
        orderTransactionModel.setProductsAndOrderedQuantity(null);

        Exception sixteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setProductsAndOrderedQuantity(new HashMap<>());

        Exception seventeenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setProductsAndOrderedQuantity(products);

        HashMap<ProductModel, Long> map = new HashMap<>();
        map.put(null, null);

        orderTransactionModel.setProductsAndOrderedQuantity(map);

        Exception eighteenthException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        ProductModel productModel2 = new ProductModel();
        productModel2.setId(ID_OF_PRODUCT_THAT_NOT_EXIST);
        HashMap<ProductModel, Long> map2 = new HashMap<>();
        map2.put(productModel2, ORDERED_QUANTITY);
        orderTransactionModel.setProductsAndOrderedQuantity(map2);

        Exception nineteenthException = assertThrows(ProductNotFoundException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setProductsAndOrderedQuantity(products);
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

        HashMap<ProductModel, Long> products2 = new HashMap<>();
        products2.put(productModel, RANDOM_QUANTITY + 10L);

        orderTransactionModel.setProductsAndOrderedQuantity(products2);

        Exception twentyThirdException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        });

        orderTransactionModel.setProductsAndOrderedQuantity(products);

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
}
