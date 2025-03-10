package org.example.backend.controller;

import org.example.backend.dao.service.TransactionDataService;
import org.example.backend.enumerated.ReturnCause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @GetMapping("/all-return-causes")
    public ResponseEntity<List<String>> getAllReturnCauses() {

        List<ReturnCause> returnCauses = List.of(ReturnCause.WRONG_SIZE, ReturnCause.DAMAGED,
                ReturnCause.LOW_QUALITY, ReturnCause.PROBLEMS_WITH_WORK, ReturnCause.MISLEADING_DATA,
                ReturnCause.CHANGED_MIND);

        List<String> returnCauseNames = new ArrayList<>();

        returnCauses.forEach(returnCause -> returnCauseNames.add(ReturnCause.toString(returnCause)));

        return ResponseEntity.status(HttpStatus.OK).body(returnCauseNames);
    }
}
