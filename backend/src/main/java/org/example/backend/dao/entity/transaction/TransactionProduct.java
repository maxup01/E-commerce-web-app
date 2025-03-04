package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

//This is super class for OrderedProduct and ReturnedProduct
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class TransactionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Double pricePerUnit;

    public TransactionProduct(Long quantity, Double pricePerUnit) {
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }
}
