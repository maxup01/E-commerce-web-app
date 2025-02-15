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
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.exception.transaction.PaymentMethodNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.OrderTransactionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    public OrderTransaction saveNewOrderTransaction(OrderTransactionModel orderTransactionModel) {

        if(orderTransactionModel == null)
            throw new BadArgumentException("Null argument: orderTransactionModel");
        else if((orderTransactionModel.getUserEmail() == null) || (!userEmailPattern.matcher(orderTransactionModel.getUserEmail()).matches()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.userEmail");
        else if((orderTransactionModel.getDeliveryProviderName() == null) || (orderTransactionModel.getDeliveryProviderName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.deliveryProviderName");
        else if((orderTransactionModel.getPaymentMethodName() == null) || (orderTransactionModel.getPaymentMethodName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.paymentMethodName");
        else if((orderTransactionModel.getAddressModel() == null) || (orderTransactionModel.getAddressModel().getCountry() == null)
                || (orderTransactionModel.getAddressModel().getProvince() == null) || (orderTransactionModel.getAddressModel().getCity() == null)
                || (orderTransactionModel.getAddressModel().getAddress() == null) || (orderTransactionModel.getAddressModel().getCountry().trim().isEmpty())
                || (orderTransactionModel.getAddressModel().getProvince().trim().isEmpty()) || (orderTransactionModel.getAddressModel().getCity().trim().isEmpty())
                || (orderTransactionModel.getAddressModel().getAddress().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.addressModel");
        else if((orderTransactionModel.getProductsAndOrderedQuantity() == null) || (orderTransactionModel.getProductsAndOrderedQuantity().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");

        ArrayList<OrderedProduct> orderedProducts = new ArrayList<>();

        orderTransactionModel.getProductsAndOrderedQuantity().forEach((productModel, quantity) -> {

            if((productModel == null) || (productModel.getId() == null) || (quantity == null) || (quantity <= 0))
                throw new BadArgumentException("Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");

            Product foundProduct = productRepository.findById(productModel.getId()).orElseThrow(() -> {
                return new ProductNotFoundException("Product with id " + productModel.getId() + " not found");
            });

            if(foundProduct.getStock().getQuantity() < quantity){
                throw new BadArgumentException("There is not enough stock for product with id " + productModel.getId());
            }

            orderedProducts.add(new OrderedProduct(foundProduct, quantity, foundProduct.getCurrentPrice()));
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
                orderTransactionModel.getAddressModel().getCountry(), orderTransactionModel.getAddressModel().getProvince(),
                orderTransactionModel.getAddressModel().getCity(), orderTransactionModel.getAddressModel().getAddress());

        if(deliveryAddress == null){

            deliveryAddress = new Address(orderTransactionModel.getAddressModel().getCountry(), orderTransactionModel.getAddressModel().getProvince(),
                    orderTransactionModel.getAddressModel().getCity(), orderTransactionModel.getAddressModel().getAddress());

            deliveryAddress = addressRepository.save(deliveryAddress);
        }

        orderedProductRepository.saveAll(orderedProducts);

        OrderTransaction orderTransaction = new OrderTransaction(Date.from(Instant.now()), user, deliveryAddress,
                deliveryProvider, paymentMethod, orderedProducts);

        orderTransactionRepository.save(orderTransaction);

        orderedProducts.forEach(orderedProduct -> {
            orderedProduct = orderedProductRepository.save(orderedProduct);
            orderedProduct.setOrderTransaction(orderTransaction);
        });

        orderedProducts.forEach(orderedProduct -> {
            orderedProduct.getProduct().getStock().setQuantity(orderedProduct.getProduct().getStock().getQuantity() - orderedProduct.getQuantity());
        });

        return orderTransaction;
    }

    @Transactional
    public OrderTransaction getOrderTransactionById(UUID id) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return orderTransactionRepository.findById(id).orElseThrow(() -> {
            return new OrderTransactionNotFoundException("Order transaction with id " + id + " not found");
        });
    }

    @Transactional
    public Long getCountOfAllOrderTransactionsByTimePeriod(Date startingDate, Date endingDate) {

        if((startingDate == null) || (!startingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if((endingDate == null) || (!endingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");

        return orderTransactionRepository.getCountOfAllOrderTransactionsByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriod(Date startingDate, Date endingDate) {

        if((startingDate == null) || (!startingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if((endingDate == null) || (!endingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");

        return orderTransactionRepository.findOrderTransactionByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriodAndPaymentMethodName(Date startingDate, Date endingDate,
                                                                                       String paymentMethodName) {

        if((startingDate == null) || (!startingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if((endingDate == null) || (!endingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");
        else if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");

        return orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndPaymentMethodName(startingDate, endingDate, paymentMethodName);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriodAndDeliveryProviderName(Date startingDate, Date endingDate,
                                                                                          String deliveryProviderName) {

        if((startingDate == null) || (!startingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if((endingDate == null) || (!endingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        return orderTransactionRepository.findOrderTransactionsByTimePeriodAndDeliveryProviderName(startingDate, endingDate, deliveryProviderName);
    }

    @Transactional
    public List<OrderTransaction> getOrderTransactionsByTimePeriodAndUserEmail(Date startingDate, Date endingDate, String userEmail) {

        if((startingDate == null) || (!startingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if((endingDate == null) || (!endingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
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
}
