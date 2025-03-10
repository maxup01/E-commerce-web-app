package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.dao.entity.transaction.ReturnedProduct;
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
import org.example.backend.model.*;
import org.example.backend.validator.DateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ReturnTransactionService {

    private final AddressRepository addressRepository;
    private final DeliveryProviderRepository deliveryProviderRepository;
    private final ReturnTransactionRepository returnTransactionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderedProductRepository orderedProductRepository;
    private final ReturnedProductRepository returnedProductRepository;

    private final Pattern userEmailPattern;
    private final Pattern ean8Pattern;
    private final Pattern ean13Pattern;

    @Autowired
    public ReturnTransactionService(DeliveryProviderRepository deliveryProviderRepository, ReturnTransactionRepository returnTransactionRepository,
                                    UserRepository userRepository, ProductRepository productRepository,
                                    OrderedProductRepository orderedProductRepository, AddressRepository addressRepository,
                                    ReturnedProductRepository returnedProductRepository) {
        this.deliveryProviderRepository = deliveryProviderRepository;
        this.returnTransactionRepository = returnTransactionRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderedProductRepository = orderedProductRepository;
        this.returnedProductRepository = returnedProductRepository;
        this.addressRepository = addressRepository;
        this.userEmailPattern = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-z]+");
        this.ean8Pattern = Pattern.compile("^[0-9]{8}$");
        this.ean13Pattern = Pattern.compile("^[0-9]{13}$");
    }

    @Transactional
    public ReturnTransactionModel saveNewReturnTransaction(ReturnTransactionModel returnTransactionModel) {

        if(returnTransactionModel == null)
            throw new BadArgumentException("Incorrect argument: returnTransactionModel");
        else if((returnTransactionModel.getUserEmail() == null)
                || (!userEmailPattern.matcher(returnTransactionModel.getUserEmail()).matches()))
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.userEmail");
        else if((returnTransactionModel.getAddress() == null) || (returnTransactionModel.getAddress().getCountry() == null)
                || (returnTransactionModel.getAddress().getCountry().trim().isEmpty())
                || (returnTransactionModel.getAddress().getProvince() == null)
                || (returnTransactionModel.getAddress().getProvince().trim().isEmpty())
                || (returnTransactionModel.getAddress().getCity() == null)
                || (returnTransactionModel.getAddress().getCity().trim().isEmpty())
                || (returnTransactionModel.getAddress().getAddress() == null)
                || (returnTransactionModel.getAddress().getAddress().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.addressModel");
        else if((returnTransactionModel.getDeliveryProviderName() == null) || (returnTransactionModel.getDeliveryProviderName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.deliveryProviderName");
        else if(returnTransactionModel.getReturnCause() == null)
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.returnCause");
        else if(returnTransactionModel.getReturnedProducts() == null)
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");

        List<ReturnedProduct> productsToReturn = new ArrayList<>();

        returnTransactionModel.getReturnedProducts().forEach(returnedProductModel -> {

            Long quantityNotReturned = 0L;

            if((returnedProductModel.getProduct() == null) || (returnedProductModel.getProduct().getEANCode() == null)
                    || ((!ean8Pattern.matcher(returnedProductModel.getProduct().getEANCode()).matches()) &&
                            (!ean13Pattern.matcher(returnedProductModel.getProduct().getEANCode()).matches()))
                    || (returnedProductModel.getQuantity() == null) || (returnedProductModel.getQuantity() <= 0)
                    || (returnedProductModel.getTransactionInWhichThisProductWasOrdered() == null))
                throw new BadArgumentException("Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");

            Product foundProduct = productRepository.findByEANCode(returnedProductModel.getProduct().getEANCode());

            if(foundProduct == null)
                throw new ProductNotFoundException(
                        "Product with ean code " + returnedProductModel.getProduct().getEANCode() + " not found");

            List<OrderedProduct> orderedProducts = orderedProductRepository
                    .getAllProductsAndTheirOrderedQuantityAndPricePerUnitByTimePeriodAndTransactionId(
                            Date.from(Instant.now().minus(14, ChronoUnit.DAYS)),
                            Date.from(Instant.now()), returnedProductModel.getTransactionInWhichThisProductWasOrdered());

            List<ReturnedProduct> returnedProducts = returnedProductRepository
                    .getReturnedProductByTimePeriodAndTransactionId(
                            Date.from(Instant.now().minus(14, ChronoUnit.DAYS)),
                            Date.from(Instant.now()), returnedProductModel.getTransactionInWhichThisProductWasOrdered());

            orderedProducts.removeIf(orderedProduct -> {
                return (!orderedProduct.getProduct().getName().equals(foundProduct.getName()));
            });

            returnedProducts.removeIf(returnedProduct -> {
                return (!returnedProduct.getProduct().getName().equals(foundProduct.getName()));
            });

            if(orderedProducts.isEmpty())
                throw new BadArgumentException("Transaction with id " +
                        returnedProductModel.getTransactionInWhichThisProductWasOrdered() + " doesn't have product which you want to return");

            quantityNotReturned += orderedProducts.get(0).getQuantity();

            quantityNotReturned -= returnedProducts.stream()
                    .mapToLong(ReturnedProduct::getQuantity).sum();

            if(returnedProductModel.getQuantity() > quantityNotReturned)
                throw new BadArgumentException("One of products can't be returned cause of too big quantity");

            productsToReturn.add(new ReturnedProduct(foundProduct, returnedProductModel.getQuantity(),
                    orderedProducts.get(0).getPricePerUnit(), returnedProductModel.getTransactionInWhichThisProductWasOrdered()));
        });

        User user = userRepository.findByEmail(returnTransactionModel.getUserEmail());

        if(user == null)
            throw new UserNotFoundException("User with email" + returnTransactionModel.getUserEmail() + " not found");

        DeliveryProvider deliveryProvider = deliveryProviderRepository
                .findByName(returnTransactionModel.getDeliveryProviderName());

        if(deliveryProvider == null)
            throw new DeliveryProviderNotFoundException(
                    "Delivery Provider with name " + returnTransactionModel.getDeliveryProviderName() + " not found");

        List<ReturnedProduct> returnedProductData = returnedProductRepository.saveAll(productsToReturn);

        AddressModel address = returnTransactionModel.getAddress();
        Address entityAddress = addressRepository.findByCountryAndCityAndProvinceAndAddress(
                address.getCountry(), address.getProvince(), address.getCity(), address.getAddress());

        if(entityAddress == null){
            entityAddress = addressRepository.save(new Address(address.getCountry(), address.getProvince(),
                    address.getCity(), address.getAddress()));
        }

        ReturnTransaction returnTransaction = new ReturnTransaction(Date.from(Instant.now()), user,
                entityAddress, deliveryProvider, returnTransactionModel.getReturnCause(), returnedProductData);

        returnTransaction = returnTransactionRepository.save(returnTransaction);

        return ReturnTransactionModel.fromReturnTransaction(returnTransaction);
    }

    @Transactional
    public ReturnTransactionModel updateReturnTransactionStatusById(UUID id, TransactionStatus status) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((status == null) || (status == TransactionStatus.PAID) || (status == TransactionStatus.PREPARED))
            throw new BadArgumentException("Incorrect argument: status");

        ReturnTransaction returnTransaction = returnTransactionRepository.findById(id).orElseThrow(() -> {
            return new ReturnTransactionNotFoundException("Return transaction with id " + id + " not found");
        });

        returnTransaction.setStatus(status);
        returnTransactionRepository.save(returnTransaction);

        return ReturnTransactionModel.fromReturnTransaction(returnTransaction);
    }

    @Transactional
    public ReturnTransactionModel getReturnTransactionById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        ReturnTransaction returnTransactionFound = returnTransactionRepository.findById(id).orElseThrow(() -> {
            return new ReturnTransactionNotFoundException("Return transaction with id " + id + " not found");
        });

        return ReturnTransactionModel.fromReturnTransaction(returnTransactionFound);
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByIdList(
            List<UUID> returnTransactionIds){

        if((returnTransactionIds == null) || (returnTransactionIds.isEmpty()))
            throw new BadArgumentException("Incorrect argument: returnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByReturnTransactionIds(returnTransactionIds);

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactions(
            List<UUID> forbiddenReturnTransactionIds){

        if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactions(forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriod(
            Date startingDate, Date endingDate, List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriod(
                        startingDate, endingDate, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCause(
            ReturnCause returnCause, List<UUID> forbiddenReturnTransactionIds){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByReturnCause(
                        returnCause, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByDeliveryProviderName(
            String deliveryProviderName, List<UUID> forbiddenReturnTransactionIds){

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByDeliveryProviderName(
                        deliveryProviderName, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByUserEmail(
            String userEmail, List<UUID> forbiddenReturnTransactionIds){

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByUserEmail(
                        userEmail, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndReturnCause(
            Date startingDate, Date endingDate, ReturnCause returnCause,
            List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCause(
                        startingDate, endingDate, returnCause, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndDeliveryProviderName(
            Date startingDate, Date endingDate, String deliveryProviderName,
            List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndDeliveryProviderName(
                        startingDate, endingDate, deliveryProviderName, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndUserEmail(
            Date startingDate, Date endingDate, String userEmail, List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndUserEmail(
                        startingDate, endingDate, userEmail, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCauseAndDeliveryProviderName(
            ReturnCause returnCause, String deliveryProviderName, List<UUID> forbiddenReturnTransactionIds){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndDeliveryProviderName(
                        returnCause, deliveryProviderName, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCauseAndUserEmail(
            ReturnCause returnCause, String userEmail, List<UUID> forbiddenReturnTransactionIds){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndUserEmail(
                        returnCause, userEmail, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByDeliveryProviderNameAndUserEmail(
            String deliveryProviderName, String userEmail, List<UUID> forbiddenReturnTransactionIds){

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByDeliveryProviderNameAndUserEmail(
                        deliveryProviderName, userEmail, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
            Date startingDate, Date endingDate, ReturnCause returnCause, String deliveryProviderName,
            List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                        startingDate, endingDate, returnCause, deliveryProviderName,
                        forbiddenReturnTransactionIds, PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
            Date startingDate, Date endingDate, ReturnCause returnCause, String userEmail,
            List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                        startingDate, endingDate, returnCause, userEmail, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
            Date startingDate, Date endingDate, String deliveryProviderName, String userEmail,
            List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                        startingDate, endingDate, deliveryProviderName, userEmail,
                        forbiddenReturnTransactionIds, PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
            ReturnCause returnCause, String deliveryProviderName, String userEmail,
            List<UUID> forbiddenReturnTransactionIds){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                        returnCause, deliveryProviderName, userEmail, forbiddenReturnTransactionIds,
                        PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    //This method returns list of maximum 24 ReturnTransaction
    @Transactional
    public List<ReturnTransactionModel>
    getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
            Date startingDate, Date endingDate, ReturnCause returnCause,
            String deliveryProviderName, String userEmail, List<UUID> forbiddenReturnTransactionIds){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");
        else if(forbiddenReturnTransactionIds == null)
            throw new BadArgumentException("Null argument: forbiddenReturnTransactionIds");

        List<ReturnTransaction> returnTransactions = returnTransactionRepository
                .findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                        startingDate, endingDate, returnCause, deliveryProviderName, userEmail,
                        forbiddenReturnTransactionIds, PageRequest.of(0, 24));

        return mapReturnTransactionListToReturnTransactionModelList(returnTransactions);
    }

    @Transactional
    public List<Object[]> getQuantityOfAllReturnedProductsAndRevenue(){
        return returnedProductRepository.getAllQuantityOfReturnedProductsAndRevenue();
    }

    @Transactional
    public List<Object[]> getAllTypesAndTheirReturnedQuantityAndRevenue(){
        return returnedProductRepository.getAllTypesAndTheirReturnedQuantityAndRevenue();
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(
            Date startingDate, Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(startingDate, endingDate);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByPhrase(phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByType(String type){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByType(type);

        if(resultList.isEmpty())
            throw new ReturnedProductNotFoundException("Returned product with type " + type + " not exist");

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
            Date startingDate, Date endingDate, String phrase){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                        startingDate, endingDate, phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
            Date startingDate, Date endingDate, String type){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                        startingDate, endingDate, type);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndType(
            String phrase, String type){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");
        else if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndType(
                        phrase, type);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndType(
            Date startingDate, Date endingDate, String phrase, String type){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");
        else if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Object[]> resultList = returnedProductRepository
                .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndType(
                        startingDate, endingDate, phrase, type);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
    }

    @Transactional
    public Object[] getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(Date startingDate,
                                                                                 Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        List<Object[]> resultList = returnedProductRepository
                .getAllQuantityOfReturnedProductsAndRevenueByTimePeriod(startingDate, endingDate);

        Object[] returnList = new Object[2];
        returnList[0] = resultList.get(0)[0];
        returnList[1] = resultList.get(0)[1];

        return returnList;
    }

    @Transactional
    public List<Object[]> getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(Date startingDate,
                                                                                    Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return returnedProductRepository
                .getAllTypesAndTheirQuantityOfReturnedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    private List<Object[]> mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(List<Object[]> list){

        ArrayList<Object[]> resultListWithProductModel = new ArrayList<>();

        list.forEach(row -> {

            Object[] newRow = new Object[3];
            newRow[0] = ProductModel.fromProduct((Product) row[0]);
            newRow[1] = row[1];
            newRow[2] = row[2];

            resultListWithProductModel.add(newRow);
        });

        return resultListWithProductModel;
    }

    private List<ReturnTransactionModel> mapReturnTransactionListToReturnTransactionModelList(
            List<ReturnTransaction> returnTransactionList){

        List<ReturnTransactionModel> returnTransactionModels = new ArrayList<>();

        returnTransactionList.forEach(returnTransaction -> {
            returnTransactionModels.add(ReturnTransactionModel.fromReturnTransaction(returnTransaction));
        });

        return returnTransactionModels;
    }
}
