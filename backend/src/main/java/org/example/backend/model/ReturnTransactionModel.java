package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enumerated.ReturnCause;
import org.example.backend.enumerated.TransactionStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnTransactionModel {

    private UUID id;
    private String  firstNameAndLastName;
    private String  userEmail;
    private AddressModel addressModel;
    private TransactionStatus transactionStatus;
    private Date transactionDate;
    private String deliveryProviderName;
    private ReturnCause returnCause;
    private HashMap<ProductModel, Long> productsAndReturnedQuantity;
}
