package org.example.backend.dao.service;

import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.dao.repository.transaction.ReturnTransactionRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
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
public class ReturnTransactionServiceTest {

    private final UUID ID_OF_RETURN_TRANSACTION_THAT_EXISTS = UUID.randomUUID();
    private final UUID ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS = UUID.randomUUID();

    @Mock
    private ReturnTransactionRepository returnTransactionRepository;

    @InjectMocks
    private ReturnTransactionService returnTransactionService;

    private ReturnTransaction returnTransaction;

    @BeforeEach
    public void setUp() {
        returnTransaction = new ReturnTransaction();
    }

    @Test
    public void testOfGetReturnTransactionById(){

        when(returnTransactionRepository.findById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS))
                .thenReturn(Optional.ofNullable(returnTransaction));
        when(returnTransactionRepository.findById(ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS))
                .thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            returnTransactionService.getReturnTransactionById(null);
        });

        Exception secondException = assertThrows(ReturnTransactionNotFoundException.class, () -> {
            returnTransactionService.getReturnTransactionById(ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS);
        });

        assertDoesNotThrow(() -> {
            returnTransactionService.getReturnTransactionById(ID_OF_RETURN_TRANSACTION_THAT_EXISTS);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Return transaction with id " + ID_OF_RETURN_TRANSACTION_THAT_NOT_EXISTS + " not found");
    }
}
