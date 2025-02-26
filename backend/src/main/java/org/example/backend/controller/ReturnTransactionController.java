package org.example.backend.controller;

import org.example.backend.dao.service.ReturnTransactionService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.model.ReturnTransactionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ReturnTransactionController{

    private final ReturnTransactionService returnTransactionService;

    @Autowired
    public ReturnTransactionController(ReturnTransactionService returnTransactionService) {
        this.returnTransactionService = returnTransactionService;
    }

    @GetMapping("/return")
    public ResponseEntity<ReturnTransactionModel> getReturnTransactionById(
            @RequestBody UUID id) {

        ReturnTransactionModel returnTransactionModel;

        try{
            returnTransactionModel = returnTransactionService.getReturnTransactionById(id);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ReturnTransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(returnTransactionModel);
    }
}
