package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.repository.transaction.OrderTransactionRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

//TODO change return types to data models
@Service
public class OrderTransactionService {

    private final OrderTransactionRepository orderTransactionRepository;

    @Autowired
    public OrderTransactionService(OrderTransactionRepository orderTransactionRepository) {
        this.orderTransactionRepository = orderTransactionRepository;
    }

    @Transactional
    public OrderTransaction getOrderTransactionById(UUID id) {

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return orderTransactionRepository.findById(id).orElseThrow(() -> {
            return new OrderTransactionNotFoundException("Order transaction with id " + id + " not found");
        });
    }
}
