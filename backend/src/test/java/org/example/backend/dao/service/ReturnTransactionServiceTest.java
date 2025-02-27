package org.example.backend.dao.service;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.UserImage;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.dao.entity.user.Role;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.logistic.AddressRepository;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.dao.repository.transaction.OrderedProductRepository;
import org.example.backend.dao.repository.transaction.ReturnTransactionRepository;
import org.example.backend.dao.repository.transaction.ReturnedProductRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.enumerated.ReturnCause;
import org.example.backend.enumerated.TransactionStatus;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.exception.transaction.ReturnedProductNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.AddressModel;
import org.example.backend.model.ProductModel;
import org.example.backend.model.ReturnTransactionModel;
import org.example.backend.model.ReturnedProductModel;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReturnTransactionServiceTest {

    private final UUID ID_OF_RETURN_TRANSACTION_THAT_EXISTS = UUID.randomUUID();
    private final UUID ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS = UUID.randomUUID();
    private final String RANDOM_EMAIL = "random@email.com";
    private final String DIFFERENT_EMAIL = "not@exist.com";
    private final String WRONG_EMAIL = "wrongemail.com";
    private final String COUNTRY_NAME = "countryName";
    private final String PROVINCE_NAME = "provinceName";
    private final String CITY_NAME = "cityName";
    private final String ADDRESS = "address";
    private final String DELIVERY_PROVIDER_NAME = "deliveryProviderName";
    private final String DIFFERENT_DELIVERY_PROVIDER_NAME = "HAAIFAIFIA";
    private final ReturnCause RETURN_CAUSE = ReturnCause.DAMAGED;
    private final Long RETURNED_QUANTITY = 1L;
    private final Long NEGATIVE_QUANTITY = -2L;
    private final UUID ID_OF_PRODUCT_THAT_EXISTS = UUID.randomUUID();
    private final UUID ID_OF_PRODUCT_THAT_NOT_EXISTS = UUID.randomUUID();
    private final String RANDOM_EAN_CODE = "73920483";
    private final String RANDOM_PRODUCT_NAME = "random product name";
    private final String RANDOM_PRODUCT_TYPE = "random type";
    private final String RANDOM_PRODUCT_DESCRIPTION = "random description";
    private final Double RANDOM_PRODUCT_PRICE = 15.00;
    private final Double GREATER_PRODUCT_PRICE = 15.00;
    private final Integer RANDOM_PRODUCT_HEIGHT = 100;
    private final Integer RANDOM_PRODUCT_WIDTH = 80;
    private final byte[] RANDOM_IMAGE = new byte[12];
    private final UUID ID_OF_ORDER_TRANSACTION_THAT_EXISTS = UUID.randomUUID();
    private final UUID ID_OF_ORDER_TRANSACTION_THAT_NOT_EXISTS = UUID.randomUUID();
    private final Long RANDOM_QUANTITY = 34L;
    private final Double RANDOM_PRICE = 10.00;
    private final Long GREATER_QUANTITY = 1000L;
    private final String PRODUCT_NAME = "productName";
    private final TransactionStatus RANDOM_STATUS = TransactionStatus.DELIVERED;
    private final TransactionStatus WRONG_STATUS_FOR_RETURN_TRANSACTION = TransactionStatus.PAID;
    private final TransactionStatus SECOND_WRONG_STATUS_FOR_RETURN_TRANSACTION = TransactionStatus.PREPARED;
    private final String RANDOM_PHRASE = "randomPhrase";
    private final String DIFFERENT_PHRASE = "differentPhrase";
    private final String TYPE_THAT_NOT_EXIST = "differentType";
    private final String TYPE_THAT_EXIST = "randomType";
    private final Date DATE_BEFORE = new Date(0);
    private final Date DATE_NOW = Date.from(Instant.now());
    private final Date DATE_AFTER = new Date(10000000000000000L);
    private final String RANDOM_FIRST_NAME = "random first name";
    private final String RANDOM_LAST_NAME = "random last name";
    private final String RANDOM_PASSWORD = "random password";
    private final Role RANDOM_ROLE = new Role();
    private final UserImage USER_IMAGE = new UserImage(new byte[12]);
    private final LocalDate BIRTH_DATE = LocalDate.of(2020, 1, 1);

    @Mock
    private OrderedProductRepository orderedProductRepository;

    @Mock
    private ReturnedProductRepository returnedProductRepository;

    @Mock
    private ReturnTransactionRepository returnTransactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeliveryProviderRepository deliveryProviderRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private ReturnTransactionService returnTransactionService;

    private ReturnTransaction returnTransaction;
    private ReturnTransactionModel returnTransactionModel;
    private ProductModel productModel;
    private Product product;
    private OrderedProduct orderedProduct;
    private User user;

    @BeforeEach
    public void setUp() {
        AddressModel addressModel = new AddressModel(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS);
        returnTransactionModel = new ReturnTransactionModel();
        returnTransactionModel.setUserEmail(RANDOM_EMAIL);
        returnTransactionModel.setAddress(addressModel);
        returnTransactionModel.setDeliveryProviderName(DELIVERY_PROVIDER_NAME);
        returnTransactionModel.setReturnCause(RETURN_CAUSE);

        productModel = ProductModel
                .builder()
                .id(ID_OF_PRODUCT_THAT_EXISTS)
                .name(PRODUCT_NAME)
                .build();

        user = new User(RANDOM_FIRST_NAME, RANDOM_LAST_NAME, RANDOM_EMAIL, RANDOM_PASSWORD,
                BIRTH_DATE, RANDOM_ROLE, USER_IMAGE);

        product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_PRODUCT_TYPE, RANDOM_PRODUCT_DESCRIPTION,
                RANDOM_PRODUCT_HEIGHT, RANDOM_PRODUCT_WIDTH, GREATER_PRODUCT_PRICE,
                RANDOM_PRODUCT_PRICE, new Stock(RANDOM_QUANTITY), new ProductMainImage(RANDOM_IMAGE));

        orderedProduct = new OrderedProduct(
                product, RANDOM_QUANTITY, RANDOM_PRICE);

        returnTransaction = ReturnTransaction
                .builder()
                .returnCause(RETURN_CAUSE)
                .user(user)
                .deliveryAddress(new Address(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS))
                .deliveryProvider(new DeliveryProvider(DELIVERY_PROVIDER_NAME, true))
                .build();

        returnTransaction.setId(ID_OF_RETURN_TRANSACTION_THAT_EXISTS);
        returnTransaction.setFirstNameAndLastNameOfUser(user.getFirstName() + " " + user.getLastName());
        returnTransaction.setUserEmail(user.getEmail());
        returnTransaction.setStatus(RANDOM_STATUS);
        returnTransaction.setDate(DATE_NOW);
        returnTransaction.setCost(RANDOM_PRICE);
        returnTransaction.setReturnedProducts(new ArrayList<>());
    }

    @Test
    public void testOfSaveNewReturnTransaction(){

        when(productRepository.findById(ID_OF_PRODUCT_THAT_EXISTS))
                .thenReturn(Optional.ofNullable(product));
        when(productRepository.findById(ID_OF_PRODUCT_THAT_NOT_EXISTS))
                .thenReturn(Optional.empty());

        when(orderedProductRepository
                .getAllProductsAndTheirOrderedQuantityAndPricePerUnitByTimePeriodAndTransactionId(
                        any(Date.class), any(Date.class), eq(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXISTS)
                )).thenReturn(new ArrayList<>());

        ArrayList<OrderedProduct> list = new ArrayList<>();
        list.add(orderedProduct);

        when(orderedProductRepository
                .getAllProductsAndTheirOrderedQuantityAndPricePerUnitByTimePeriodAndTransactionId(
                        any(Date.class), any(Date.class), eq(ID_OF_ORDER_TRANSACTION_THAT_EXISTS)
                )).thenReturn(list);

        when(returnedProductRepository
                .getReturnedProductByTimePeriodAndTransactionId(
                        any(Date.class), any(Date.class), any(UUID.class)
                )).thenReturn(new ArrayList<>());

        when(userRepository.findByEmail(RANDOM_EMAIL)).thenReturn(user);
        when(userRepository.findByEmail(DIFFERENT_EMAIL)).thenReturn(null);

        when(deliveryProviderRepository.findByName(DELIVERY_PROVIDER_NAME)).thenReturn(new DeliveryProvider());
        when(deliveryProviderRepository.findByName(DIFFERENT_DELIVERY_PROVIDER_NAME)).thenReturn(null);

        when(addressRepository.findByCountryAndCityAndProvinceAndAddress(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS))
                .thenReturn(null);

        when(returnTransactionRepository.save(any(ReturnTransaction.class))).thenReturn(returnTransaction);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(null);
        });

        returnTransactionModel.setUserEmail(null);

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setUserEmail(WRONG_EMAIL);

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setUserEmail(RANDOM_EMAIL);
        returnTransactionModel.getAddress().setCountry(null);

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setCountry("");

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setCountry(COUNTRY_NAME);
        returnTransactionModel.getAddress().setProvince(null);

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setProvince("");

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setProvince(PROVINCE_NAME);
        returnTransactionModel.getAddress().setCity(null);

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setCity("");

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setCity(CITY_NAME);
        returnTransactionModel.getAddress().setAddress(null);

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setAddress("");

        Exception eleventhException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddress().setAddress(ADDRESS);
        returnTransactionModel.setDeliveryProviderName(null);

        Exception twelfthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setDeliveryProviderName("");

        Exception thirteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setDeliveryProviderName(DELIVERY_PROVIDER_NAME);
        returnTransactionModel.setReturnCause(null);

        Exception fourteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setReturnCause(RETURN_CAUSE);
        returnTransactionModel.setReturnedProducts(null);

        Exception fifteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        ReturnedProductModel returnedProductModel = ReturnedProductModel
                .builder()
                .product(null)
                .quantity(RETURNED_QUANTITY)
                .transactionInWhichThisProductWasOrdered(ID_OF_RETURN_TRANSACTION_THAT_EXISTS)
                .build();

        ArrayList<ReturnedProductModel> returnedProductModels = new ArrayList<>();
        returnedProductModels.add(returnedProductModel);

        returnTransactionModel.setReturnedProducts(returnedProductModels);

        Exception sixteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(null);

        returnedProductModel.setProduct(productModel);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception seventeenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(ID_OF_PRODUCT_THAT_EXISTS);

        returnedProductModel.setProduct(productModel);
        returnedProductModel.setQuantity(null);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception eighteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnedProductModel.setQuantity(NEGATIVE_QUANTITY);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception nineteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(ID_OF_PRODUCT_THAT_NOT_EXISTS);

        returnedProductModel.setProduct(productModel);
        returnedProductModel.setQuantity(RETURNED_QUANTITY);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception twentiethException = assertThrows(ProductNotFoundException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(ID_OF_PRODUCT_THAT_EXISTS);
        returnedProductModel.setProduct(productModel);
        returnedProductModel.setTransactionInWhichThisProductWasOrdered(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXISTS);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception twentyFirstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnedProductModel.setTransactionInWhichThisProductWasOrdered(ID_OF_ORDER_TRANSACTION_THAT_EXISTS);
        returnedProductModel.setQuantity(GREATER_QUANTITY);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception twentySecondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setUserEmail(DIFFERENT_EMAIL);

        returnedProductModel.setProduct(productModel);
        returnedProductModel.setQuantity(RANDOM_QUANTITY);

        returnedProductModels.clear();
        returnedProductModels.add(returnedProductModel);

        Exception twentyThirdException = assertThrows(UserNotFoundException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setUserEmail(RANDOM_EMAIL);
        returnTransactionModel.setDeliveryProviderName(DIFFERENT_DELIVERY_PROVIDER_NAME);

        Exception twentyFourthException = assertThrows(DeliveryProviderNotFoundException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setDeliveryProviderName(DELIVERY_PROVIDER_NAME);

        assertDoesNotThrow(() -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: returnTransactionModel");
        assertEquals(secondException.getMessage(), "Incorrect argument field: returnTransactionModel.userEmail");
        assertEquals(thirdException.getMessage(), "Incorrect argument field: returnTransactionModel.userEmail");
        assertEquals(fourthException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(fifthException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(sixthException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(seventhException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(eighthException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(ninthException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(tenthException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(eleventhException.getMessage(), "Incorrect argument field: returnTransactionModel.addressModel");
        assertEquals(twelfthException.getMessage(), "Incorrect argument field: returnTransactionModel.deliveryProviderName");
        assertEquals(thirteenthException.getMessage(), "Incorrect argument field: returnTransactionModel.deliveryProviderName");
        assertEquals(fourteenthException.getMessage(), "Incorrect argument field: returnTransactionModel.returnCause");
        assertEquals(fifteenthException.getMessage(), "Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");
        assertEquals(sixteenthException.getMessage(), "Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");
        assertEquals(seventeenthException.getMessage(), "Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");
        assertEquals(eighteenthException.getMessage(), "Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");
        assertEquals(nineteenthException.getMessage(), "Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");
        assertEquals(twentiethException.getMessage(), "Product with id " + ID_OF_PRODUCT_THAT_NOT_EXISTS + " not found");
        assertEquals(twentyFirstException.getMessage(), "Transaction with id " + ID_OF_ORDER_TRANSACTION_THAT_NOT_EXISTS + " doesn't have product which you want to return");
        assertEquals(twentySecondException.getMessage(), "One of products can't be returned cause of too big quantity");
        assertEquals(twentyThirdException.getMessage(), "User with email" + DIFFERENT_EMAIL + " not found");
        assertEquals(twentyFourthException.getMessage(), "Delivery Provider with name " + DIFFERENT_DELIVERY_PROVIDER_NAME + " not found");
    }

    @Test
    public void testOfUpdateReturnTransactionStatusById(){

        when(returnTransactionRepository.findById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS))
                .thenReturn(Optional.ofNullable(returnTransaction));
        when(returnTransactionRepository.findById(ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS))
                .thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.updateReturnTransactionStatusById(null, RANDOM_STATUS);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.updateReturnTransactionStatusById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS,
                    null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.updateReturnTransactionStatusById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS,
                    WRONG_STATUS_FOR_RETURN_TRANSACTION);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.updateReturnTransactionStatusById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS,
                    SECOND_WRONG_STATUS_FOR_RETURN_TRANSACTION);
        });

            Exception fifthException = assertThrows(ReturnTransactionNotFoundException.class, () -> {
            returnTransactionService.updateReturnTransactionStatusById(ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS,
                    RANDOM_STATUS);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.updateReturnTransactionStatusById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS,
                    RANDOM_STATUS);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: status");
        assertEquals(thirdException.getMessage(), "Incorrect argument: status");
        assertEquals(fourthException.getMessage(), "Incorrect argument: status");
        assertEquals(fifthException.getMessage(), "Return transaction with id " + ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS + " not found");
    }

    @Test
    public void testOfGetReturnTransactionById(){

        when(returnTransactionRepository.findById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS))
                .thenReturn(Optional.ofNullable(returnTransaction));
        when(returnTransactionRepository.findById(ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS))
                .thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionById(null);
        });

        Exception secondException = assertThrows(ReturnTransactionNotFoundException.class, () -> {
            returnTransactionService.getReturnTransactionById(ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Return transaction with id " + ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS + " not found");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriod(){

        when(returnTransactionRepository.findReturnTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByTimePeriod(null, DATE_AFTER);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByTimePeriod(DATE_BEFORE, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByTimePeriod(DATE_AFTER, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionsByTimePeriod(DATE_BEFORE, DATE_AFTER);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetReturnTransactionsByReturnCause(){

        when(returnTransactionRepository.findReturnTransactionsByReturnCause(RETURN_CAUSE))
                .thenReturn(List.of(returnTransaction));

        Exception exception = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByReturnCause(null);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionsByReturnCause(RETURN_CAUSE);
        });

        assertEquals(exception.getMessage(), "Null argument: returnCause");
    }

    @Test
    public void testOfGetReturnTransactionsByDeliveryProviderName(){

        when(returnTransactionRepository.findReturnTransactionsByDeliveryProviderName(DELIVERY_PROVIDER_NAME))
            .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByDeliveryProviderName(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByDeliveryProviderName("");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionsByDeliveryProviderName(DELIVERY_PROVIDER_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(secondException.getMessage(), "Incorrect argument: deliveryProviderName");
    }

    @Test
    public void testOfGetReturnTransactionsByUserEmail(){

        when(returnTransactionRepository.findReturnTransactionsByUserEmail(RANDOM_EMAIL))
            .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByUserEmail(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByUserEmail(WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionsByUserEmail(RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(secondException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndReturnCause(){

        when(returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCause(DATE_BEFORE, DATE_AFTER, RETURN_CAUSE))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCause(null, DATE_AFTER, RETURN_CAUSE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCause(DATE_BEFORE, null, RETURN_CAUSE);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCause(DATE_AFTER, DATE_BEFORE, RETURN_CAUSE);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCause(DATE_BEFORE, DATE_AFTER, null);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCause(DATE_BEFORE, DATE_AFTER, RETURN_CAUSE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Null argument: returnCause");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndDeliveryProviderName(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                            null, DATE_AFTER, DELIVERY_PROVIDER_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                            DATE_BEFORE, null, DELIVERY_PROVIDER_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                            DATE_AFTER, DATE_BEFORE, DELIVERY_PROVIDER_NAME);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, "");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, DELIVERY_PROVIDER_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(fifthException.getMessage(), "Incorrect argument: deliveryProviderName");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndUserEmail(null, DATE_AFTER, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, null, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndUserEmail(DATE_AFTER, DATE_BEFORE, RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndUserEmail(DATE_BEFORE, DATE_AFTER, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(fifthException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByReturnCauseAndDeliveryProviderName(){

        when(returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndDeliveryProviderName(RETURN_CAUSE, DELIVERY_PROVIDER_NAME))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderName(
                            null, DELIVERY_PROVIDER_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderName(
                            RETURN_CAUSE, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderName(
                            RETURN_CAUSE, "");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderName(
                            RETURN_CAUSE, DELIVERY_PROVIDER_NAME);
        });

        assertEquals(firstException.getMessage(), "Null argument: returnCause");
        assertEquals(secondException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(thirdException.getMessage(), "Incorrect argument: deliveryProviderName");
    }

    @Test
    public void testOfGetReturnTransactionsByReturnCauseAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndUserEmail(RETURN_CAUSE, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByReturnCauseAndUserEmail(null, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByReturnCauseAndUserEmail(RETURN_CAUSE, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionsByReturnCauseAndUserEmail(RETURN_CAUSE, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionsByReturnCauseAndUserEmail(RETURN_CAUSE, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Null argument: returnCause");
        assertEquals(secondException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(thirdException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByDeliveryProviderNameAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByDeliveryProviderNameAndUserEmail(DELIVERY_PROVIDER_NAME, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByDeliveryProviderNameAndUserEmail(null, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByDeliveryProviderNameAndUserEmail("", RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByDeliveryProviderNameAndUserEmail(DELIVERY_PROVIDER_NAME, null);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByDeliveryProviderNameAndUserEmail(DELIVERY_PROVIDER_NAME, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByDeliveryProviderNameAndUserEmail(DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(secondException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(thirdException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(fourthException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(){

        when(returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                        DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            null, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            DATE_BEFORE, null, RETURN_CAUSE, DELIVERY_PROVIDER_NAME);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            DATE_AFTER, DATE_BEFORE, RETURN_CAUSE, DELIVERY_PROVIDER_NAME);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, null, DELIVERY_PROVIDER_NAME);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, null);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, "");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Null argument: returnCause");
        assertEquals(fifthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(sixthException.getMessage(), "Incorrect argument: deliveryProviderName");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                        DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            null, DATE_AFTER, RETURN_CAUSE, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            DATE_BEFORE, null, RETURN_CAUSE, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            DATE_AFTER, DATE_BEFORE, RETURN_CAUSE, RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, null, RANDOM_EMAIL);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, null);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Null argument: returnCause");
        assertEquals(fifthException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(sixthException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                        DATE_BEFORE, DATE_AFTER, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            null, DATE_AFTER, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, null, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            DATE_AFTER, DATE_BEFORE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, null, RANDOM_EMAIL);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, "", RANDOM_EMAIL);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, DELIVERY_PROVIDER_NAME, null);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                                DATE_BEFORE, DATE_AFTER, DELIVERY_PROVIDER_NAME, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(fifthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(sixthException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(seventhException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                        RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                            null, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                            RETURN_CAUSE, null, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                            RETURN_CAUSE, "", RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                            RETURN_CAUSE, DELIVERY_PROVIDER_NAME, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                            RETURN_CAUSE, DELIVERY_PROVIDER_NAME, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                            RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Null argument: returnCause");
        assertEquals(secondException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(thirdException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(fourthException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(fifthException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(){

        when(returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                        DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL))
                .thenReturn(List.of(returnTransaction));

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            null, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, null, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_AFTER, DATE_BEFORE, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, null, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, null, RANDOM_EMAIL);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, "", RANDOM_EMAIL);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, null);
        });

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                            DATE_BEFORE, DATE_AFTER, RETURN_CAUSE, DELIVERY_PROVIDER_NAME, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Null argument: returnCause");
        assertEquals(fifthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(sixthException.getMessage(), "Incorrect argument: deliveryProviderName");
        assertEquals(seventhException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(eighthException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetQuantityOfAllReturnedProductsAndRevenue(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByPhrase(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByPhrase("");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByPhrase(RANDOM_PHRASE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: phrase");
        assertEquals(secondException.getMessage(), "Incorrect argument: phrase");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(){

        Object[] row = new Object[3];
        row[0] = product;
        row[1] = RANDOM_QUANTITY;
        row[2] = RANDOM_PRICE;

        ArrayList<Object[]> result = new ArrayList<>();
        result.add(row);

        when(returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER))
                .thenReturn(result);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(null, DATE_AFTER);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(DATE_BEFORE, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(DATE_AFTER, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(DATE_BEFORE, DATE_AFTER);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByPhrase(){

        Object[] row = new Object[3];
        row[0] = product;
        row[1] = RANDOM_QUANTITY;
        row[2] = RANDOM_PRICE;

        ArrayList<Object[]> result = new ArrayList<>();
        result.add(row);

        when(returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByPhrase(RANDOM_PHRASE))
                .thenReturn(result);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByPhrase(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByPhrase("");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByPhrase(RANDOM_PHRASE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: phrase");
        assertEquals(secondException.getMessage(), "Incorrect argument: phrase");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByType(){

        ArrayList<Object[]> arrayList = new ArrayList<>();

        Object[] row = new Object[3];
        row[0] = product;
        row[1] = RANDOM_QUANTITY;
        row[2] = RANDOM_PRICE;

        arrayList.add(row);

        when(returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByType(TYPE_THAT_EXIST))
                .thenReturn(arrayList);

        when(returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByType(TYPE_THAT_NOT_EXIST))
                .thenReturn(new ArrayList<>());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByType(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByType("");
        });

        Exception thirdException = assertThrows(ReturnedProductNotFoundException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByType(TYPE_THAT_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByType(TYPE_THAT_EXIST);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: type");
        assertEquals(secondException.getMessage(), "Incorrect argument: type");
        assertEquals(thirdException.getMessage(), "Returned product with type " + TYPE_THAT_NOT_EXIST + " not exist");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByUserEmail(){

        ArrayList<Object[]> arrayList = new ArrayList<>();

        Object[] row = new Object[3];
        row[0] = product;
        row[1] = RANDOM_QUANTITY;
        row[2] = RANDOM_PRICE;

        arrayList.add(row);

        when(returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByUserEmail(RANDOM_EMAIL))
                .thenReturn(arrayList);

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByUserEmail(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByUserEmail(WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getProductsAndTheirReturnedQuantityAndRevenueByUserEmail(RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(secondException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                            null, DATE_NOW, RANDOM_PHRASE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                            DATE_BEFORE, null, RANDOM_PHRASE);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                            DATE_NOW, DATE_BEFORE, RANDOM_PHRASE);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                            DATE_BEFORE, DATE_NOW, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                            DATE_BEFORE, DATE_NOW, "");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                            DATE_BEFORE, DATE_NOW, RANDOM_PHRASE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: phrase");
        assertEquals(fifthException.getMessage(), "Incorrect argument: phrase");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                            null, DATE_NOW, TYPE_THAT_EXIST);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                            DATE_BEFORE, null, TYPE_THAT_EXIST);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                            DATE_NOW, DATE_BEFORE, TYPE_THAT_EXIST);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                            DATE_BEFORE, DATE_NOW, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                            DATE_BEFORE, DATE_NOW, "");
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                            DATE_BEFORE, DATE_NOW, TYPE_THAT_EXIST);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: type");
        assertEquals(fifthException.getMessage(), "Incorrect argument: type");
    }

    @Test
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                            null, DATE_NOW, RANDOM_EMAIL);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                            DATE_BEFORE, null, RANDOM_EMAIL);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                            DATE_NOW, DATE_BEFORE, RANDOM_EMAIL);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                            DATE_BEFORE, DATE_NOW, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                            DATE_BEFORE, DATE_NOW, WRONG_EMAIL);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService
                    .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
                            DATE_BEFORE, DATE_NOW, RANDOM_EMAIL);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
        assertEquals(fourthException.getMessage(), "Incorrect argument: userEmail");
        assertEquals(fifthException.getMessage(), "Incorrect argument: userEmail");
    }

    @Test
    public void testOfGetQuantityOfAllReturnedProductsAndRevenueByTimePeriod(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(
                    null, DATE_NOW);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(
                    DATE_BEFORE, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(
                    DATE_NOW, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(
                    DATE_BEFORE, DATE_NOW);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
    }

    @Test
    public void testOfGetAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(
                    null, DATE_NOW);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(
                    DATE_BEFORE, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(
                    DATE_NOW, DATE_BEFORE);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(
                    DATE_BEFORE, DATE_NOW);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: startingDate");
        assertEquals(secondException.getMessage(), "Incorrect argument: endingDate");
        assertEquals(thirdException.getMessage(), "Argument startingDate is after endingDate");
    }
}
