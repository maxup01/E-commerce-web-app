package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enumerated.TransactionStatus;

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
    private String firstNameAndLastNameOfUser;

    @Column(nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Double cost;

    public Transaction(Date date, TransactionStatus transactionStatus, String firstNameAndLastName,
                       String userEmail, Double cost) {
        this.date = date;
        this.status = transactionStatus;
        this.firstNameAndLastNameOfUser = firstNameAndLastName;
        this.userEmail = userEmail;
        this.cost = cost;
    }
}
