package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.enumerated.TransactionStatus;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransactionIdAndTransactionStatusModel {

    private UUID transactionId;
    private TransactionStatus transactionStatus;
}
