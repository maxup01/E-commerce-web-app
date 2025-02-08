package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, UUID> {

    //TODO finish this repo
}
