package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

//This is super class for OrderTransaction and ReturnTransaction entities
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Date transactionDate;

    public Transaction(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
