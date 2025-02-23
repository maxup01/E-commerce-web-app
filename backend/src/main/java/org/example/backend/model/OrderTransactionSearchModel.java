package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class OrderTransactionSearchModel {

    private Date startingDate;
    private Date endingDate;
    private String paymentMethodName;
    private String deliveryProviderName;
    private String userEmail;
}
