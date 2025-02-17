package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
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
import org.example.backend.validator.DateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

//TODO change return types to data models
@Service
public class OrderTransactionService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final DeliveryProviderRepository deliveryProviderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderedProductRepository orderedProductRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final UserRepository userRepository;

    private final Pattern userEmailPattern;

    @Autowired
    public OrderTransactionService(AddressRepository addressRepository, ProductRepository productRepository,
                                   OrderedProductRepository orderedProductRepository, OrderTransactionRepository orderTransactionRepository,
                                   UserRepository userRepository, DeliveryProviderRepository deliveryProviderRepository,
                                   PaymentMethodRepository paymentMethodRepository) {
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderedProductRepository = orderedProductRepository;
        this.orderTransactionRepository = orderTransactionRepository;
        this.userRepository = userRepository;
        this.userEmailPattern = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-z]+");
        this.deliveryProviderRepository = deliveryProviderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Transactional
    public OrderTransactionModel saveNewOrderTransaction(OrderTransactionModel orderTransactionModel) {

        if(orderTransactionModel == null)
            throw new BadArgumentException("Null argument: orderTransactionModel");
        else if((orderTransactionModel.getUserEmail() == null) || (!userEmailPattern.matcher(orderTransactionModel.getUserEmail()).matches()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.userEmail");
        else if((orderTransactionModel.getDeliveryProviderName() == null) || (orderTransactionModel.getDeliveryProviderName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.deliveryProviderName");
        else if((orderTransactionModel.getPaymentMethodName() == null) || (orderTransactionModel.getPaymentMethodName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.paymentMethodName");
        else if((orderTransactionModel.getAddress() == null) || (orderTransactionModel.getAddress().getCountry() == null)
                || (orderTransactionModel.getAddress().getProvince() == null) || (orderTransactionModel.getAddress().getCity() == null)
                || (orderTransactionModel.getAddress().getAddress() == null) || (orderTransactionModel.getAddress().getCountry().trim().isEmpty())
                || (orderTransactionModel.getAddress().getProvince().trim().isEmpty()) || (orderTransactionModel.getAddress().getCity().trim().isEmpty())
                || (orderTransactionModel.getAddress().getAddress().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.addressModel");
        else if((orderTransactionModel.getOrderedProducts() == null) || (orderTransactionModel.getOrderedProducts().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");

        ArrayList<OrderedProduct> orderedProducts = new ArrayList<>();

        orderTransactionModel.getOrderedProducts().forEach(orderedProductModel -> {

            if((orderedProductModel == null) || (orderedProductModel.getProduct() == null)
                    || (orderedProductModel.getProduct().getId() == null)
                    || (orderedProductModel.getQuantity() == null) || (orderedProductModel.getQuantity() <= 0))
                throw new BadArgumentException("Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");

            Product foundProduct = productRepository.findById(orderedProductModel.getProduct().getId()).orElseThrow(() -> {
                return new ProductNotFoundException("Product with id " + orderedProductModel.getProduct().getId() + " not found");
            });

            if(foundProduct.getStock().getQuantity() < orderedProductModel.getQuantity()){
                throw new BadArgumentException("There is not enough stock for product with id " + orderedProductModel.getProduct().getId());
            }

            orderedProducts.add(new OrderedProduct(foundProduct, orderedProductModel.getQuantity(),
                    foundProduct.getCurrentPrice()));
        });

        User user = userRepository.findByEmail(orderTransactionModel.getUserEmail());

        if(user == null)
            throw new UserNotFoundException("User with email " + orderTransactionModel.getUserEmail() + " not found");

        DeliveryProvider deliveryProvider = deliveryProviderRepository.findByName(orderTransactionModel.getDeliveryProviderName());

        if(deliveryProvider == null)
            throw new DeliveryProviderNotFoundException("Delivery Provider with name " + orderTransactionModel.getDeliveryProviderName() + " not found");

        PaymentMethod paymentMethod = paymentMethodRepository.findByName(orderTransactionModel.getPaymentMethodName());

        if(paymentMethod == null)
            throw new PaymentMethodNotFoundException("Payment Method with name " + orderTransactionModel.getPaymentMethodName() + " not found");

        Address deliveryAddress = addressRepository.findByCountryAndCityAndProvinceAndAddress(
                orderTransactionModel.getAddress().getCountry(), orderTransactionModel.getAddress().getProvince(),
                orderTransactionModel.getAddress().getCity(), orderTransactionModel.getAddress().getAddress());

        if(deliveryAddress == null){

            deliveryAddress = new Address(orderTransactionModel.getAddress().getCountry(),
                    orderTransactionModel.getAddress().getProvince(), orderTransactionModel.getAddress().getCity(),
                    orderTransactionModel.getAddress().getAddress());

            deliveryAddress = addressRepository.save(deliveryAddress);
        }

        ArrayList<OrderedProduct> savedOrderedProducts =
                new ArrayList<>(orderedProductRepository.saveAll(orderedProducts));

        OrderTransaction orderTransaction = new OrderTransaction(Date.from(Instant.now()), user, deliveryAddress,
                deliveryProvider, paymentMethod, orderedProducts);

        OrderTransaction finalOrderTransaction = orderTransactionRepository.save(orderTransaction);

        savedOrderedProducts.forEach(orderedProduct -> {
            orderedProduct = orderedProductRepository.save(orderedProduct);
            orderedProduct.setOrderTransaction(finalOrderTransaction);
        });

        savedOrderedProducts.forEach(orderedProduct -> {
            orderedProduct.getProduct().getStock()
                    .setQuantity(orderedProduct.getProduct().getStock().getQuantity() - orderedProduct.getQuantity());
        });

        return mapOrderTransactionEntityToModel(orderTransaction);
    }

    @Transactional
    public OrderTransactionModel updateOrderTransactionStatusById(UUID id, TransactionStatus status) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((status == null) || (status == TransactionStatus.ACCEPTED_RETURN))
            throw new BadArgumentException("Incorrect argument: status");

        OrderTransaction orderTransaction = orderTransactionRepository.findById(id).orElseThrow(() -> {
            return new OrderTransactionNotFoundException("Order transaction with id " + id + " not found");
        });

        orderTransaction.setStatus(status);

        return mapOrderTransactionEntityToModel(orderTransaction);
    }

    @Transactional
    public OrderTransactionModel getOrderTransactionById(UUID id) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        OrderTransaction orderTransaction = orderTransactionRepository.findById(id).orElseThrow(() -> {
            return new OrderTransactionNotFoundException("Order transaction with id " + id + " not found");
        });

        return mapOrderTransactionEntityToModel(orderTransaction);
    }

    @Transactional
    public Long getCountOfAllOrderTransactionsByTimePeriod(Date startingDate, Date endingDate) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderTransactionRepository.getCountOfAllOrderTransactionsByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriod(Date startingDate, Date endingDate) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderTransactionRepository.findOrderTransactionByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriodAndPaymentMethodName(Date startingDate, Date endingDate,
                                                                                       String paymentMethodName) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");

        return orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndPaymentMethodName(startingDate, endingDate, paymentMethodName);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriodAndDeliveryProviderName(Date startingDate, Date endingDate,
                                                                                          String deliveryProviderName) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        return orderTransactionRepository.findOrderTransactionsByTimePeriodAndDeliveryProviderName(startingDate, endingDate,
                deliveryProviderName);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriodAndUserEmail(Date startingDate, Date endingDate,
                                                                               String userEmail) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return orderTransactionRepository.findOrderTransactionsByTimePeriodAndUserEmail(startingDate, endingDate, userEmail);
    }

    @Transactional
    public List<Object[]> getAllQuantityOfOrderedProductsAndRevenue(){
        return orderedProductRepository.getAllQuantityOfOrderedProductsAndRevenue();
    }

    @Transactional
    public List<Object[]> getAllTypesAndTheirOrderedQuantityAndRevenue(){
        return orderedProductRepository.getAllTypesAndTheirOrderedQuantityAndRevenue();
    }

    @Transactional
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        return orderedProductRepository.getProductsAndTheirOrderedQuantityAndRevenueByPhrase(phrase);
    }

    @Transactional
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByType(String type){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        return orderedProductRepository.getProductsAndTheirOrderedQuantityAndRevenueByType(type);
    }

    @Transactional
    List<Object[]> getQuantityOfOrderedProductsAndRevenueByTimePeriod(Date startingDate, Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderedProductRepository.getAllQuantityOfOrderedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    List<Object[]> getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(Date startingDate,
                                                                                Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderedProductRepository
                .getAllTypesAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
            Date startingDate, Date endingDate, String phrase){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        return orderedProductRepository
                .getAllProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndPhrase(startingDate, endingDate, phrase);
    }

    private OrderTransactionModel mapOrderTransactionEntityToModel(OrderTransaction orderTransaction){

        OrderTransactionModel orderTransactionReturnModel = OrderTransactionModel
                .builder()
                .id(orderTransaction.getId())
                .firstNameAndLastName(orderTransaction.getUser().getFirstName() + " " + orderTransaction.getUser().getLastName())
                .userEmail(orderTransaction.getUser().getEmail())
                .transactionDate(orderTransaction.getDate())
                .deliveryProviderName(orderTransaction.getDeliveryProvider().getName())
                .paymentMethodName(orderTransaction.getPaymentMethod().getName())
                .build();

        AddressModel addressModel = new AddressModel(orderTransaction.getDeliveryAddress().getCountry(),
                orderTransaction.getDeliveryAddress().getProvince(), orderTransaction.getDeliveryAddress().getCity(),
                orderTransaction.getDeliveryAddress().getAddress());

        orderTransactionReturnModel.setAddress(addressModel);

        ArrayList<OrderedProductModel> orderedProductModels = new ArrayList<>();

        orderTransaction.getOrderedProducts().forEach(orderedProduct -> {

            Product product = orderedProduct.getProduct();

            ProductModel productModel = new ProductModel(product.getId(), product.getEANCode(), product.getName(),
                    product.getType(), product.getDescription(), product.getHeight(), product.getWidth(),
                    product.getRegularPrice(), product.getCurrentPrice(), product.getMainImage().getImage());

            orderedProductModels.add(new OrderedProductModel(orderedProduct.getId(), productModel,
                    orderedProduct.getQuantity()));
        });

        orderTransactionReturnModel.setOrderedProducts(orderedProductModels);

        return orderTransactionReturnModel;
    }
}
