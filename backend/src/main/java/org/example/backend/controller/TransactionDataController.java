package org.example.backend.controller;

import org.example.backend.dao.service.TransactionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionDataController {

    private final TransactionDataService transactionDataService;

    @Autowired
    public TransactionDataController(TransactionDataService transactionDataService) {
        this.transactionDataService = transactionDataService;
    }

    @GetMapping("/all-delivery-provider-names")
    public ResponseEntity<List<String>> getAllDeliveryProviderNames() {

        List<String> deliveryProviderNames = transactionDataService.getAllDeliveryProvidersNames();

        return ResponseEntity.status(HttpStatus.OK).body(deliveryProviderNames);
    }

    @GetMapping("/all-payment-method-names")
    public ResponseEntity<List<String>> getAllPaymentMethodNames() {

        List<String> paymentMethodNames = transactionDataService.getAllPaymentMethodsNames();

        return ResponseEntity.status(HttpStatus.OK).body(paymentMethodNames);
    }
}
