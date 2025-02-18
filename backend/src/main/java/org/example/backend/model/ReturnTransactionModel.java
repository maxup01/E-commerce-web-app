package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.enumerated.ReturnCause;
import org.example.backend.enumerated.TransactionStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnTransactionModel {

    private UUID id;
    private String  firstNameAndLastName;
    private String  userEmail;
    private AddressModel address;
    private TransactionStatus transactionStatus;
    private Date transactionDate;
    private String deliveryProviderName;
    private ReturnCause returnCause;
    private ArrayList<ReturnedProductModel> returnedProducts;

    public static ReturnTransactionModel fromReturnTransaction(ReturnTransaction returnTransaction){

        ReturnTransactionModel returnTransactionReturnModel = ReturnTransactionModel
                .builder()
                .id(returnTransaction.getId())
                .firstNameAndLastName(returnTransaction.getUser().getFirstName() + " " + returnTransaction.getUser().getLastName())
                .userEmail(returnTransaction.getUser().getEmail())
                .transactionStatus(returnTransaction.getStatus())
                .transactionDate(returnTransaction.getDate())
                .deliveryProviderName(returnTransaction.getDeliveryProvider().getName())
                .returnCause(returnTransaction.getReturnCause())
                .build();

        AddressModel addressModel = new AddressModel(returnTransaction.getDeliveryAddress().getCountry(),
                returnTransaction.getDeliveryAddress().getProvince(), returnTransaction.getDeliveryAddress().getCity(),
                returnTransaction.getDeliveryAddress().getAddress());

        returnTransactionReturnModel.setAddress(addressModel);

        ArrayList<ReturnedProductModel> returnedProductModels = new ArrayList<>();

        returnTransaction.getReturnedProducts().forEach(returnedProduct -> {

            Product product = returnedProduct.getProduct();

            ProductModel productModel = ProductModel.fromProduct(product);

            returnedProductModels.add(new ReturnedProductModel(returnedProduct.getId(), productModel,
                    returnedProduct.getQuantity(), returnedProduct.getOrderTransactionId()));
        });

        returnTransactionReturnModel.setReturnedProducts(returnedProductModels);

        return returnTransactionReturnModel;
    }
}
