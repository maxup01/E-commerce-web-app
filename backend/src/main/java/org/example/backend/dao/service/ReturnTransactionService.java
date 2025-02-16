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
import org.example.backend.enumerated.TransactionStatus;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.AddressModel;
import org.example.backend.model.ReturnTransactionModel;
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
    public ReturnTransaction saveNewReturnTransaction(ReturnTransactionModel returnTransactionModel) {

        if(returnTransactionModel == null)
            throw new BadArgumentException("Incorrect argument: returnTransactionModel");
        else if((returnTransactionModel.getUserEmail() == null)
                || (!userEmailPattern.matcher(returnTransactionModel.getUserEmail()).matches()))
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.userEmail");
        else if((returnTransactionModel.getAddressModel() == null) || (returnTransactionModel.getAddressModel().getCountry() == null)
                || (returnTransactionModel.getAddressModel().getCountry().trim().isEmpty())
                || (returnTransactionModel.getAddressModel().getProvince() == null)
                || (returnTransactionModel.getAddressModel().getProvince().trim().isEmpty())
                || (returnTransactionModel.getAddressModel().getCity() == null)
                || (returnTransactionModel.getAddressModel().getCity().trim().isEmpty())
                || (returnTransactionModel.getAddressModel().getAddress() == null)
                || (returnTransactionModel.getAddressModel().getAddress().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.addressModel");
        else if((returnTransactionModel.getDeliveryProviderName() == null) || (returnTransactionModel.getDeliveryProviderName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.deliveryProviderName");
        else if(returnTransactionModel.getReturnCause() == null)
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.returnCause");
        else if(returnTransactionModel.getProductsAndReturnedQuantity() == null)
            throw new BadArgumentException("Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");

        List<ReturnedProduct> productsToReturn = new ArrayList<>();

        returnTransactionModel.getProductsAndReturnedQuantity().forEach((productModel, quantity) -> {

            Long quantityNotReturned = 0L;

            if((productModel == null) || (productModel.getId() == null) || (quantity == null) || (quantity <= 0)
                    || (productModel.getOrderTransactionId() == null))
                throw new BadArgumentException("Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");

            Product foundProduct = productRepository.findById(productModel.getId()).orElseThrow(() -> {
                return new ProductNotFoundException("Product with id " + productModel.getId() + " not found");
            });

            List<OrderedProduct> orderedProducts = orderedProductRepository
                    .getAllProductsAndTheirOrderedQuantityAndPricePerUnitByTimePeriodAndTransactionId(
                            Date.from(Instant.now().minus(14, ChronoUnit.DAYS)),
                            Date.from(Instant.now()), productModel.getOrderTransactionId());

            List<ReturnedProduct> returnedProducts = returnedProductRepository
                    .getAllProductsAndTheirReturnedQuantityAndPricePerUnitByTimePeriodAndTransactionId(
                            Date.from(Instant.now().minus(14, ChronoUnit.DAYS)),
                            Date.from(Instant.now()), productModel.getOrderTransactionId());

            orderedProducts.removeIf(orderedProduct -> {
                return (!orderedProduct.getProduct().getName().equals(foundProduct.getName()));
            });

            returnedProducts.removeIf(returnedProduct -> {
                return (!returnedProduct.getProduct().getName().equals(foundProduct.getName()));
            });

            if(orderedProducts.isEmpty())
                throw new BadArgumentException("Transaction with id " + productModel.getOrderTransactionId() + " doesn't have product which you want to return");

            quantityNotReturned += orderedProducts.get(0).getQuantity();

            quantityNotReturned -= returnedProducts.stream()
                    .mapToLong(ReturnedProduct::getQuantity).sum();

            if(quantity > quantityNotReturned)
                throw new BadArgumentException("One of products can't be returned cause of too big quantity");

            productsToReturn.add(new ReturnedProduct(foundProduct, quantity, orderedProducts.get(0).getPricePerUnit(),
                    productModel.getOrderTransactionId()));
        });

        User user = userRepository.findByEmail(returnTransactionModel.getUserEmail());

        if(user == null)
            throw new UserNotFoundException("User with email" + returnTransactionModel.getUserEmail() + " not found");

        DeliveryProvider deliveryProvider = deliveryProviderRepository.findByName(returnTransactionModel.getDeliveryProviderName());

        if(deliveryProvider == null)
            throw new DeliveryProviderNotFoundException("Delivery Provider with name " + returnTransactionModel.getDeliveryProviderName() + " not found");

        List<ReturnedProduct> returnedProductData = returnedProductRepository.saveAll(productsToReturn);

        AddressModel address = returnTransactionModel.getAddressModel();
        Address entityAddress = addressRepository.findByCountryAndCityAndProvinceAndAddress(
                address.getCountry(), address.getProvince(), address.getCity(), address.getAddress());

        if(entityAddress == null){
            entityAddress = addressRepository.save(new Address(address.getCountry(), address.getProvince(),
                    address.getCity(), address.getAddress()));
        }

        return new ReturnTransaction(Date.from(Instant.now()), user, entityAddress, deliveryProvider,
                returnTransactionModel.getReturnCause(), returnedProductData);
    }

    @Transactional
    public ReturnTransaction updateReturnTransactionStatusById(UUID id, TransactionStatus status) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((status == null) || (status == TransactionStatus.PAID) || (status == TransactionStatus.PREPARED))
            throw new BadArgumentException("Incorrect argument: status");

        ReturnTransaction returnTransaction = returnTransactionRepository.findById(id).orElseThrow(() -> {
            return new ReturnTransactionNotFoundException("Return transaction with id " + id + " not found");
        });

        returnTransaction.setStatus(status);
        return returnTransactionRepository.save(returnTransaction);
    }

    @Transactional
    public ReturnTransaction getReturnTransactionById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return returnTransactionRepository.findById(id).orElseThrow(() -> {
            return new ReturnTransactionNotFoundException("Return transaction with id " + id + " not found");
        });
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
    public List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        return returnedProductRepository.getProductsAndTheirReturnedQuantityAndRevenueByPhrase(phrase);
    }
}
