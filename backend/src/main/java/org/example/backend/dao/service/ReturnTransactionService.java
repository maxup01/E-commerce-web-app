package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.dao.entity.user.User;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.dao.repository.transaction.OrderTransactionRepository;
import org.example.backend.dao.repository.transaction.ReturnTransactionRepository;
import org.example.backend.dao.repository.user.UserRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.ReturnTransactionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ReturnTransactionService {

    private DeliveryProviderRepository deliveryProviderRepository;
    private ReturnTransactionRepository returnTransactionRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private OrderTransactionRepository orderTransactionRepository;

    private final Pattern userEmailPattern;

    @Autowired
    public ReturnTransactionService(DeliveryProviderRepository deliveryProviderRepository, ReturnTransactionRepository returnTransactionRepository,
                                    UserRepository userRepository, ProductRepository productRepository,
                                    OrderTransactionRepository orderTransactionRepository) {
        this.deliveryProviderRepository = deliveryProviderRepository;
        this.returnTransactionRepository = returnTransactionRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderTransactionRepository = orderTransactionRepository;
        this.userEmailPattern = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-z]+");
    }

    @Transactional
    public ReturnTransaction saveNewReturnTransaction(ReturnTransactionModel returnTransactionModel, UUID orderId) {

        if(returnTransactionModel == null)
            throw new BadArgumentException("Incorrect argument: returnTransactionModel");
        else if((returnTransactionModel.getUserEmail() == null)
                || (userEmailPattern.matcher(returnTransactionModel.getUserEmail()).matches()))
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

        List<OrderedProduct> orderedProducts = new ArrayList<>();

        returnTransactionModel.getProductsAndReturnedQuantity().forEach((productModel, quantity) -> {

            if((productModel == null) || (productModel.getId() == null) || (quantity == null) || (quantity <= 0))
                throw new BadArgumentException("Incorrect argument field: returnTransactionModel.productsAndReturnedQuantity");

            Product foundProduct = productRepository.findById(productModel.getId()).orElseThrow(() -> {
                return new ProductNotFoundException("Product with id" + productModel.getId() + " not found");
            });

            orderedProducts.add(new OrderedProduct(foundProduct, quantity, foundProduct.getCurrentPrice()));
        });

        User user = userRepository.findByEmail(returnTransactionModel.getUserEmail());

        if(user == null)
            throw new UserNotFoundException("User with email" + returnTransactionModel.getUserEmail() + " not found");

        DeliveryProvider deliveryProvider = deliveryProviderRepository.findByName(returnTransactionModel.getDeliveryProviderName());

        if(deliveryProvider == null)
            throw new DeliveryProviderNotFoundException("Delivery Provider with name " + returnTransactionModel.getDeliveryProviderName() + " not found");

        //TODO method not finished yet

        return new ReturnTransaction();
    }

    @Transactional
    public ReturnTransaction getReturnTransactionById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return returnTransactionRepository.findById(id).orElseThrow(() -> {
            return new ReturnTransactionNotFoundException("Return transaction with id " + id + " not found");
        });
    }
}
