package org.example.backend.controller;

import org.example.backend.dao.service.ReturnTransactionService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.ReturnTransactionModel;
import org.example.backend.model.ReturnTransactionSearchModel;
import org.example.backend.model.TransactionIdAndTransactionStatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ReturnTransactionController{

    private final ReturnTransactionService returnTransactionService;

    @Autowired
    public ReturnTransactionController(ReturnTransactionService returnTransactionService) {
        this.returnTransactionService = returnTransactionService;
    }

    @PostMapping("/return/create")
    public ResponseEntity<ReturnTransactionModel> createReturnTransaction(
            @RequestBody ReturnTransactionModel returnTransactionModel) {

        ReturnTransactionModel result;

        try{
            result = returnTransactionService
                    .saveNewReturnTransaction(returnTransactionModel);
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DeliveryProviderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/return/update-status-by-id")
    public ResponseEntity<ReturnTransactionModel> updateReturnTransactionStatusById(
            @RequestBody TransactionIdAndTransactionStatusModel transactionIdAndTransactionStatusModel) {

        ReturnTransactionModel returnTransactionModel;

        try{
            returnTransactionModel = returnTransactionService.updateReturnTransactionStatusById(
                    transactionIdAndTransactionStatusModel.getTransactionId(),
                    transactionIdAndTransactionStatusModel.getTransactionStatus());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ReturnTransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(returnTransactionModel);
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

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnTransactionModel>> getReturnTransactionsByReturnTransactionSearchModel(
            @RequestBody ReturnTransactionSearchModel searchModel) {

        List<ReturnTransactionModel> returnTransactionModels;

        if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getReturnCause() != null)
                && (searchModel.getDeliveryProviderName() != null)
                && (searchModel.getUserEmail() != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getReturnCause(), searchModel.getDeliveryProviderName(),
                                searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getReturnCause() != null) && (searchModel.getDeliveryProviderName() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getReturnCause(), searchModel.getDeliveryProviderName());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getReturnCause() != null) && (searchModel.getUserEmail() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getReturnCause(), searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getDeliveryProviderName() != null) && (searchModel.getUserEmail() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getDeliveryProviderName(), searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getReturnCause() != null) && (searchModel.getDeliveryProviderName() != null)
                && (searchModel.getUserEmail() != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                                searchModel.getReturnCause(), searchModel.getDeliveryProviderName(),
                                searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getReturnCause() != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCause(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getReturnCause());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getDeliveryProviderName() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getDeliveryProviderName());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)
                && (searchModel.getUserEmail() != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndUserEmail(
                                searchModel.getStartingDate(), searchModel.getEndingDate(),
                                searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getReturnCause() != null) && (searchModel.getDeliveryProviderName() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCauseAndDeliveryProviderName(
                                searchModel.getReturnCause(), searchModel.getDeliveryProviderName());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getReturnCause() != null) && (searchModel.getUserEmail() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCauseAndUserEmail(
                                searchModel.getReturnCause(), searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getDeliveryProviderName() != null) && (searchModel.getUserEmail() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByDeliveryProviderNameAndUserEmail(
                                searchModel.getDeliveryProviderName(), searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((searchModel.getStartingDate() != null) && (searchModel.getEndingDate() != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriod(
                                searchModel.getStartingDate(), searchModel.getEndingDate());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(searchModel.getReturnCause() != null){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCause(searchModel.getReturnCause());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(searchModel.getDeliveryProviderName() != null){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByDeliveryProviderName(searchModel.getDeliveryProviderName());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(searchModel.getUserEmail() != null){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByUserEmail(searchModel.getUserEmail());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(returnTransactionModels);
    }

    @GetMapping("/returns/returned-products/quantity-and-revenue")
    public ResponseEntity<Object[]> getQuantityAndRevenueOfAllReturnedProducts(){

        Object[] result = new Object[2];

        List<Object[]> quantityAndRevenue= returnTransactionService.getQuantityOfAllReturnedProductsAndRevenue();

        result[0] = quantityAndRevenue.get(0)[0];
        result[1] = quantityAndRevenue.get(0)[1];

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/returns/returned-products/types-and-related-quantity-and-revenue")
    public ResponseEntity<List<Object[]>> getAllTypesAndRelatedQuantityAndRevenueOfAllReturnedProducts(){

        return ResponseEntity.status(HttpStatus.OK).body(returnTransactionService
                .getAllTypesAndTheirReturnedQuantityAndRevenue());
    }
}
