package org.example.backend.model;

import lombok.*;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.enumerated.TransactionStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTransactionModel {

    private UUID id;
    private String  firstNameAndLastName;
    private String  userEmail;
    private AddressModel address;
    private TransactionStatus transactionStatus;
    private Date transactionDate;
    private String deliveryProviderName;
    private String paymentMethodName;
    private ArrayList<OrderedProductModel> orderedProducts;

    public static OrderTransactionModel fromOrderTransaction(OrderTransaction orderTransaction){

        OrderTransactionModel orderTransactionReturnModel = OrderTransactionModel
                .builder()
                .id(orderTransaction.getId())
                .firstNameAndLastName(orderTransaction.getUser().getFirstName() + " " + orderTransaction.getUser().getLastName())
                .userEmail(orderTransaction.getUser().getEmail())
                .transactionStatus(orderTransaction.getStatus())
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

            ProductModel productModel = new ProductModel(product.getEANCode(), product.getName(),
                    product.getType(), product.getDescription(), product.getHeight(), product.getWidth(),
                    product.getRegularPrice(), product.getCurrentPrice(), product.getMainImage().getImage());

            orderedProductModels.add(new OrderedProductModel(orderedProduct.getId(), productModel,
                    orderedProduct.getQuantity()));
        });

        orderTransactionReturnModel.setOrderedProducts(orderedProductModels);

        return orderTransactionReturnModel;
    }
}
