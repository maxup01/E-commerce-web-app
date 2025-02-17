package org.example.backend.model;

import lombok.*;
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
}
