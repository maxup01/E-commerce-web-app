package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.transaction.OrderTransaction;
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

            if((returnedProductModel.getProduct() == null) || (returnedProductModel.getProduct().getId() == null)
                    || (returnedProductModel.getQuantity() == null) || (returnedProductModel.getQuantity() <= 0)
                    || (returnedProductModel.getTransactionInWhichThisProductWasOrdered() == null))
                throw new BadArgumentException("Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");

            Product foundProduct = productRepository.findById(returnedProductModel.getProduct().getId()).orElseThrow(() -> {
                return new ProductNotFoundException("Product with id " + returnedProductModel.getProduct().getId() + " not found");
            });

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
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriod(Date startingDate, Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository.findReturnTransactionsByTimePeriod(startingDate, endingDate));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCause(ReturnCause returnCause){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository.findReturnTransactionsByReturnCause(returnCause));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByDeliveryProviderName(String deliveryProviderName){

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository.findReturnTransactionsByDeliveryProviderName(deliveryProviderName));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByUserEmail(String userEmail){

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository.findReturnTransactionsByUserEmail(userEmail));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndReturnCause(
            Date startingDate, Date endingDate, ReturnCause returnCause){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByTimePeriodAndReturnCause(startingDate, endingDate, returnCause));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndDeliveryProviderName(
            Date startingDate, Date endingDate, String deliveryProviderName){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByTimePeriodAndDeliveryProviderName(
                                startingDate, endingDate, deliveryProviderName));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndUserEmail(
            Date startingDate, Date endingDate, String userEmail){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByTimePeriodAndUserEmail(
                                startingDate, endingDate, userEmail));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCauseAndDeliveryProviderName(
            ReturnCause returnCause, String deliveryProviderName){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByReturnCauseAndDeliveryProviderName(
                                returnCause, deliveryProviderName));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByReturnCauseAndUserEmail(
            ReturnCause returnCause, String userEmail){

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByReturnCauseAndUserEmail(returnCause, userEmail));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByDeliveryProviderNameAndUserEmail(
            String deliveryProviderName, String userEmail){

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByDeliveryProviderNameAndUserEmail(deliveryProviderName, userEmail));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
            Date startingDate, Date endingDate, ReturnCause returnCause, String deliveryProviderName){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                                startingDate, endingDate, returnCause, deliveryProviderName));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
            Date startingDate, Date endingDate, ReturnCause returnCause, String userEmail){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if(returnCause == null)
            throw new BadArgumentException("Null argument: returnCause");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                                startingDate, endingDate, returnCause, userEmail));
    }

    @Transactional
    public List<ReturnTransactionModel> getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
            Date startingDate, Date endingDate, String deliveryProviderName, String userEmail){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((deliveryProviderName == null) || (deliveryProviderName.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: deliveryProviderName");
        else if((userEmail == null) || (!userEmailPattern.matcher(userEmail).matches()))
            throw new BadArgumentException("Incorrect argument: userEmail");

        return mapReturnTransactionListToReturnTransactionModelList(
                returnTransactionRepository
                        .findReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                                startingDate, endingDate, deliveryProviderName, userEmail));
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
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(String phrase){

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
    public List<Object[]> getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(Date startingDate,
                                                                                 Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return returnedProductRepository
                .getAllQuantityOfReturnedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<Object[]> getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(Date startingDate,
                                                                                    Date endingDate){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        return returnedProductRepository
                .getAllTypesAndTheirQuantityOfReturnedProductsAndRevenueByTimePeriod(startingDate, endingDate);
    }

    @Transactional
    public List<Object[]> getAllProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(Date startingDate,
                                                                                                Date endingDate,
                                                                                                String phrase){

        DateValidator.checkIfDatesAreGood(startingDate, endingDate);

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Object[]> resultList = returnedProductRepository
                .getAllProductsAndTheirQuantityOfReturnedProductsAndRevenueByTimePeriodAndPhrase(
                        startingDate, endingDate, phrase);

        return mapListRowsFromProductAndLongAndDoubleToProductModelAndLongAndDouble(resultList);
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
