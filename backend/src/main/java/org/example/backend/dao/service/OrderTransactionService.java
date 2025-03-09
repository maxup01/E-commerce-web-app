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
import org.example.backend.model.OrderTransactionModel;
import org.example.backend.model.ProductModel;
import org.example.backend.validator.DateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

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
    private final Pattern ean8Pattern;
    private final Pattern ean13Pattern;

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
        this.ean8Pattern = Pattern.compile("^[0-9]{8}$");
        this.ean13Pattern = Pattern.compile("^[0-9]{13}$");
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
                    || (orderedProductModel.getProduct().getEANCode() == null)
                    || ((!ean8Pattern.matcher(orderedProductModel.getProduct().getEANCode()).matches())
                            && (!ean13Pattern.matcher(orderedProductModel.getProduct().getEANCode()).matches()))
                    || (orderedProductModel.getQuantity() == null) || (orderedProductModel.getQuantity() <= 0))
                throw new BadArgumentException("Incorrect argument field: orderTransactionModel.productsAndOrderedQuantity");

            Product foundProduct = productRepository
                    .findByEANCode(orderedProductModel.getProduct().getEANCode());

            if(foundProduct == null){
                throw new ProductNotFoundException(
                        "Product with ean code " + orderedProductModel.getProduct().getEANCode() + " not found");
            }

            if(foundProduct.getStock().getQuantity() < orderedProductModel.getQuantity()){
                throw new BadArgumentException(
                        "There is not enough stock for product with ean code "
                                + orderedProductModel.getProduct().getEANCode());
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

        return OrderTransactionModel.fromOrderTransaction(orderTransaction);
    }

    @Transactional
    public OrderTransactionModel updateOrderTransactionStatusById(UUID id, TransactionStatus status) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((status == null) || (status == TransactionStatus.RETURN_ACCEPTED))
            throw new BadArgumentException("Incorrect argument: status");

        OrderTransaction orderTransaction = orderTransactionRepository.findById(id).orElseThrow(() -> {
            return new OrderTransactionNotFoundException("Order transaction with id " + id + " not found");
        });

        orderTransaction.setStatus(status);

        return OrderTransactionModel.fromOrderTransaction(orderTransaction);
    }

    @Transactional
    public OrderTransactionModel getOrderTransactionById(UUID id) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        OrderTransaction orderTransaction = orderTransactionRepository.findById(id).orElseThrow(() -> {
            return new OrderTransactionNotFoundException("Order transaction with id " + id + " not found");
        });

        return OrderTransactionModel.fromOrderTransaction(orderTransaction);
    }

    @Transactional
    public Long getCountOfAllOrderTransactionsByTimePeriod(Date startingDate, Date endingDate) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderTransactionRepository.getCountOfAllOrderTransactionsByTimePeriod(startingDate, endingDate);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriod(
            Date startingDate, Date endingDate, List<UUID> forbiddenOrderTransactionIds) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orderTransactions =
                orderTransactionRepository.findOrderTransactionsByTimePeriod(
                        startingDate, endingDate, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByPaymentMethodName(
            String paymentMethodName, List<UUID> forbiddenOrderTransactionIds) {

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orders = orderTransactionRepository
                .findOrderTransactionsByPaymentMethodName(
                        paymentMethodName, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orders);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByDeliveryProviderName(
            String deliveryProviderName, List<UUID> forbiddenOrderTransactionIds) {

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orders = orderTransactionRepository
                .findOrderTransactionsByDeliveryProviderName(
                        deliveryProviderName, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orders);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByUserEmail(
            String userEmail, List<UUID> forbiddenOrderTransactionIds) {

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orders = orderTransactionRepository
                .findOrderTransactionsByUserEmail(
                        userEmail, forbiddenOrderTransactionIds, PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orders);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndPaymentMethodName(
            Date startingDate, Date endingDate, String paymentMethodName,
            List<UUID> forbiddenOrderTransactionIds) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndPaymentMethodName(
                        startingDate, endingDate, paymentMethodName, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndDeliveryProviderName(
            Date startingDate, Date endingDate, String deliveryProviderName,
            List<UUID> forbiddenOrderTransactionIds) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndDeliveryProviderName(
                        startingDate, endingDate, deliveryProviderName, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndUserEmail(
            Date startingDate, Date endingDate, String userEmail, List<UUID> forbiddenOrderTransactionIds) {

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndUserEmail(
                        startingDate, endingDate, userEmail, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByPaymentMethodNameAndDeliveryProviderName(
            String paymentMethodName, String deliveryProviderName, List<UUID> forbiddenOrderTransactionIds) {

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByPaymentMethodNameAndDeliveryProviderName(
                        paymentMethodName, deliveryProviderName, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByPaymentMethodNameAndUserEmail(
            String paymentMethodName, String userEmail, List<UUID> forbiddenOrderTransactionIds) {

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenOrderTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenOrderTransactionIds");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByPaymentMethodNameAndUserEmail(
                        paymentMethodName, userEmail, forbiddenOrderTransactionIds,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByDeliveryProviderNameAndUserEmail(
            String deliveryProviderName, String userEmail) {

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByDeliveryProviderNameAndUserEmail(
                        deliveryProviderName, userEmail, PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderName(
            Date startingDate, Date endingDate, String paymentMethodName, String deliveryProviderName){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderName(
                        startingDate, endingDate, paymentMethodName, deliveryProviderName,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndPaymentMethodNameAndUserEmail(
            Date startingDate, Date endingDate, String paymentMethodName, String userEmail){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndPaymentMethodNameAndUserEmail(startingDate, endingDate,
                        paymentMethodName, userEmail, PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
            Date startingDate, Date endingDate, String deliveryProviderName, String userEmail){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                        startingDate, endingDate, deliveryProviderName, userEmail,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
            String paymentMethodName, String deliveryProviderName, String userEmail){

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
                        paymentMethodName, deliveryProviderName, userEmail,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
            Date startingDate, Date endingDate, String paymentMethodName, String deliveryProviderName,
            String userEmail){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((paymentMethodName == null) || (paymentMethodName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: paymentMethodName");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
                        paymentMethodName, deliveryProviderName, userEmail,
                        PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
    }

    //This method gets maximum 24 order transactions
    @Transactional
    public List<OrderTransactionModel> getOrderTransactionsByIdList(List<UUID> ids){

        if((ids == null) || (ids.isEmpty()))
            throw new BadArgumentException("Incorrect argument: ids");

        List<OrderTransaction> orderTransactions = orderTransactionRepository
                .findOrderTransactionsByIdList(ids, PageRequest.of(0, 24));

        return mapOrderTransactionListToOrderTransactionModelList(orderTransactions);
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
    public List<Object[]> getQuantityOfOrderedProductsAndRevenueByTimePeriod(Date startingDate, Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderedProductRepository.getAllQuantityOfOrderedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByTimePeriod(Date startingDate, Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirOrderedQuantityAndRevenueByTimePeriod(startingDate, endingDate);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> result = orderedProductRepository.getProductsAndTheirOrderedQuantityAndRevenueByPhrase(phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByType(String type){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Object[]> result = orderedProductRepository.getProductsAndTheirOrderedQuantityAndRevenueByType(type);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
            Date startingDate, Date endingDate, String phrase){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndPhrase(startingDate, endingDate, phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndType(
            Date startingDate, Date endingDate, String type){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndType(startingDate, endingDate, type);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByTypeAndPhrase(String type, String phrase){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");
        else if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTypeAndPhrase(type, phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndTypeAndPhrase(
            Date startingDate, Date endingDate, String type, String phrase){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");
        else if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> result = orderedProductRepository
                .getProductsAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriodAndTypeAndPhrase(
                        startingDate, endingDate, type, phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(result);
    }

    @Transactional
    public List<Object[]> getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(Date startingDate,
                                                                                       Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return orderedProductRepository
                .getAllTypesAndTheirQuantityOfOrderedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    private List<Object[]> mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(List<Object[]> list){

        ArrayList<Object[]> resultWithProductTurnedToProductModel = new ArrayList<>();

        list.forEach(row -> {

            Product product = (Product) row[0];

            ProductModel productModel = ProductModel.fromProduct(product);

            Object[] newRow = new Object[3];
            newRow[0] = productModel;
            newRow[1] = productModel;
            newRow[2] = productModel;

            resultWithProductTurnedToProductModel.add(newRow);
        });

        return resultWithProductTurnedToProductModel;
    }

    private List<OrderTransactionModel> mapOrderTransactionListToOrderTransactionModelList(
            List<OrderTransaction> orderTransactionList){

        List<OrderTransactionModel> orderTransactionModels = new ArrayList<>();

        orderTransactionList.forEach(orderTransaction -> {
            orderTransactionModels.add(OrderTransactionModel.fromOrderTransaction(orderTransaction));
        });

        return orderTransactionModels;
    }
}
