package org.example.backend.dao.service;

import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.repository.transaction.OrderTransactionRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderTransactionServiceTest {

    private final UUID ID_OF_ORDER_TRANSACTION_THAT_EXIST = UUID.randomUUID();
    private final UUID ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST = UUID.randomUUID();

    @Mock
    OrderTransactionRepository orderTransactionRepository;

    @InjectMocks
    OrderTransactionService orderTransactionService;

    private OrderTransaction orderTransaction;

    @BeforeEach
    public void setUp() {
        orderTransaction = new OrderTransaction();
    }

    @Test
    public void testOfGetOrderTransactionById(){

        when(orderTransactionRepository.findById(ID_OF_ORDER_TRANSACTION_THAT_EXIST))
                .thenReturn(Optional.ofNullable(orderTransaction));
        when(orderTransactionRepository.findById(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST))
                .thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            orderTransactionService.getOrderTransactionById(null);
        });

        Exception secondException = assertThrows(OrderTransactionNotFoundException.class, () -> {
            orderTransactionService.getOrderTransactionById(ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            orderTransactionService.getOrderTransactionById(ID_OF_ORDER_TRANSACTION_THAT_EXIST);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Order transaction with id " + ID_OF_ORDER_TRANSACTION_THAT_NOT_EXIST + " not found");
    }
}
