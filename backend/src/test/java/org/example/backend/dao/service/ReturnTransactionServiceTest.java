package org.example.backend.dao.service;

import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.ReturnTransaction;
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
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.exception.transaction.ReturnedProductNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.AddressModel;
import org.example.backend.model.ProductModel;
import org.example.backend.model.ReturnTransactionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private final String TYPE_THAT_NOT_EXIST = "differentType";
    private final String TYPE_THAT_EXIST = "randomType";

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
        returnTransaction = new ReturnTransaction();
        AddressModel addressModel = new AddressModel(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS);
        returnTransactionModel = new ReturnTransactionModel();
        returnTransactionModel.setUserEmail(RANDOM_EMAIL);
        returnTransactionModel.setAddressModel(addressModel);
        returnTransactionModel.setDeliveryProviderName(DELIVERY_PROVIDER_NAME);
        returnTransactionModel.setReturnCause(RETURN_CAUSE);

        productModel = ProductModel
                .builder()
                .id(ID_OF_RETURN_TRANSACTION_THAT_EXISTS)
                .name(PRODUCT_NAME)
                .orderTransactionId(ID_OF_RETURN_TRANSACTION_THAT_EXISTS)
                .build();

        product = new Product();
        product.setName(PRODUCT_NAME);

        orderedProduct = new OrderedProduct(
                product, RANDOM_QUANTITY, RANDOM_PRICE);

        user = new User();
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
                .getAllProductsAndTheirReturnedQuantityAndPricePerUnitByTimePeriodAndTransactionId(
                        any(Date.class), any(Date.class), any(UUID.class)
                )).thenReturn(new ArrayList<>());

        when(userRepository.findByEmail(RANDOM_EMAIL)).thenReturn(user);
        when(userRepository.findByEmail(DIFFERENT_EMAIL)).thenReturn(null);

        when(deliveryProviderRepository.findByName(DELIVERY_PROVIDER_NAME)).thenReturn(new DeliveryProvider());
        when(deliveryProviderRepository.findByName(DIFFERENT_DELIVERY_PROVIDER_NAME)).thenReturn(null);

        when(addressRepository.findByCountryAndCityAndProvinceAndAddress(COUNTRY_NAME, PROVINCE_NAME, CITY_NAME, ADDRESS))
                .thenReturn(null);

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
        returnTransactionModel.getAddressModel().setCountry(null);

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setCountry("");

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setCountry(COUNTRY_NAME);
        returnTransactionModel.getAddressModel().setProvince(null);

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setProvince("");

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setProvince(PROVINCE_NAME);
        returnTransactionModel.getAddressModel().setCity(null);

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setCity("");

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setCity(CITY_NAME);
        returnTransactionModel.getAddressModel().setAddress(null);

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setAddress("");

        Exception eleventhException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.getAddressModel().setAddress(ADDRESS);
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
        returnTransactionModel.setProductsAndReturnedQuantity(null);

        Exception fifteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        HashMap<ProductModel, Long> pomMap = new HashMap<>();
        pomMap.put(null, RETURNED_QUANTITY);
        returnTransactionModel.setProductsAndReturnedQuantity(pomMap);

        Exception sixteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        pomMap.clear();
        productModel.setId(null);
        pomMap.put(productModel, RETURNED_QUANTITY);

        Exception seventeenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(ID_OF_PRODUCT_THAT_EXISTS);

        pomMap.clear();
        pomMap.put(productModel, null);

        Exception eighteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        pomMap.clear();
        pomMap.put(productModel, NEGATIVE_QUANTITY);

        Exception nineteenthException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(ID_OF_PRODUCT_THAT_NOT_EXISTS);

        pomMap.clear();
        pomMap.put(productModel, RETURNED_QUANTITY);

        Exception twentiethException = assertThrows(ProductNotFoundException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setId(ID_OF_PRODUCT_THAT_EXISTS);
        productModel.setOrderTransactionId(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXISTS);

        pomMap.clear();
        pomMap.put(productModel, RETURNED_QUANTITY);

        Exception twentyFirstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        productModel.setOrderTransactionId(ID_OF_ORDER_TRANSACTION_THAT_EXISTS);

        pomMap.clear();
        pomMap.put(productModel, GREATER_QUANTITY);

        Exception twentySecondException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.saveNewReturnTransaction(returnTransactionModel);
        });

        returnTransactionModel.setUserEmail(DIFFERENT_EMAIL);
        pomMap.clear();
        pomMap.put(productModel, RANDOM_QUANTITY);

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
    public void testOfGetProductsAndTheirReturnedQuantityAndRevenueByType(){

        ArrayList<Object[]> arrayList = new ArrayList<>();
        arrayList.add(new Object[12]);

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
}
