package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.dao.repository.transaction.ReturnTransactionRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReturnTransactionService {

    @Autowired
    private ReturnTransactionRepository returnTransactionRepository;

    @Transactional
    public ReturnTransaction getReturnTransactionById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return returnTransactionRepository.findById(id).orElseThrow(() -> {
            return new ReturnTransactionNotFoundException("Return transaction with id " + id + " not found");
        });
    }
}
