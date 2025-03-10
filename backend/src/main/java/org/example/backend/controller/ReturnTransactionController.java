package org.example.backend.controller;

import org.example.backend.dao.service.ReturnTransactionService;
import org.example.backend.enumerated.ReturnCause;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.ReturnTransactionNotFoundException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class ReturnTransactionController{

    private final ReturnTransactionService returnTransactionService;

    @Autowired
    public ReturnTransactionController(ReturnTransactionService returnTransactionService) {
        this.returnTransactionService = returnTransactionService;
    }

    @PostMapping("/create-return-transaction")
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

    @PutMapping("/update-return-transaction-status-by-id")
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

    @GetMapping("/return-transaction-by-id")
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

    @GetMapping("/return-transactions-by-search")
    public ResponseEntity<List<ReturnTransactionModel>> getReturnTransactionsByReturnTransactionSearchModel(
            @RequestParam("startingDate") Date startingDate, @RequestParam("endingDate") Date endingDate,
            @RequestParam("returnCause") ReturnCause returnCause,
            @RequestParam("deliveryProviderName") String deliveryProviderName,
            @RequestParam("userEmail") String userEmail,
            @RequestParam("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds) {

        if(forbiddenReturnTransactionIds == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<ReturnTransactionModel> returnTransactionModels;

        if((startingDate != null) && (endingDate != null) && (returnCause != null)
                && (deliveryProviderName != null) && (userEmail != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
                                startingDate, endingDate, returnCause, deliveryProviderName, userEmail);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (returnCause != null) && (deliveryProviderName != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
                                startingDate, endingDate, returnCause, deliveryProviderName);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (returnCause != null) && (userEmail != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
                                startingDate, endingDate, returnCause, userEmail);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (deliveryProviderName != null) && (userEmail != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                                startingDate, endingDate, deliveryProviderName, userEmail);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((returnCause != null) && (deliveryProviderName != null) && (userEmail != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
                                returnCause, deliveryProviderName, userEmail);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null) && (returnCause != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndReturnCause(
                                startingDate, endingDate, returnCause, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (deliveryProviderName != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndDeliveryProviderName(
                                startingDate, endingDate, deliveryProviderName, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (userEmail != null)) {

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriodAndUserEmail(
                                startingDate, endingDate, userEmail, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((returnCause != null) && (deliveryProviderName != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCauseAndDeliveryProviderName(
                                returnCause, deliveryProviderName, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((returnCause != null) && (userEmail != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCauseAndUserEmail(
                                returnCause, userEmail);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((deliveryProviderName != null) && (userEmail != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByDeliveryProviderNameAndUserEmail(
                                deliveryProviderName, userEmail);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByTimePeriod(
                                startingDate, endingDate, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(returnCause != null){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByReturnCause(returnCause, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(deliveryProviderName != null){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByDeliveryProviderName(
                                deliveryProviderName, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(userEmail != null){

            try{
                returnTransactionModels = returnTransactionService
                        .getReturnTransactionsByUserEmail(userEmail, forbiddenReturnTransactionIds);
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(returnTransactionModels);
    }

    @GetMapping("/return-transactions/all/returned-products/quantity-and-revenue")
    public ResponseEntity<Object[]> getQuantityAndRevenueOfAllReturnedProducts(){

        Object[] result = new Object[2];

        List<Object[]> quantityAndRevenue= returnTransactionService.getQuantityOfAllReturnedProductsAndRevenue();

        result[0] = quantityAndRevenue.get(0)[0];
        result[1] = quantityAndRevenue.get(0)[1];

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/return-transactions/all/returned-products/types-and-related-quantity-and-revenue")
    public ResponseEntity<List<Object[]>> getAllTypesAndRelatedQuantityAndRevenueOfAllReturnedProducts(){

        return ResponseEntity.status(HttpStatus.OK).body(returnTransactionService
                .getAllTypesAndTheirReturnedQuantityAndRevenue());
    }

    @GetMapping("/return-transactions/returned-products/product-quantity-and-revenue-by-search")
    public ResponseEntity<List<Object[]>> getProductsAndTheirReturnedQuantityAndRevenueBy(
            ProductAndQuantityAndRevenueSearchModel requestBody){

        List<Object[]> result;

        if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)
                && (requestBody.getPhrase() != null) && (requestBody.getType() != null)) {

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndType(
                                requestBody.getStartingDate(), requestBody.getEndingDate(),
                                requestBody.getPhrase(), requestBody.getType());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)
                && (requestBody.getPhrase() != null)) {

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
                                requestBody.getStartingDate(), requestBody.getEndingDate(),
                                requestBody.getPhrase());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)
                && (requestBody.getType() != null)) {

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
                                requestBody.getStartingDate(), requestBody.getEndingDate(),
                                requestBody.getType());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getPhrase() != null) && (requestBody.getType() != null)){

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndType(
                                requestBody.getPhrase(), requestBody.getType());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)){

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(
                                requestBody.getStartingDate(), requestBody.getEndingDate());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(requestBody.getPhrase() != null){

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByPhrase(requestBody.getPhrase());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(requestBody.getType() != null){

            try{
                result = returnTransactionService
                        .getProductsAndTheirReturnedQuantityAndRevenueByType(requestBody.getType());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/return-transactions/returned-products/quantity-and-revenue-by-time-period")
    public ResponseEntity<Object[]> getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(
            @RequestBody TimePeriodModel requestBody){

        if(requestBody == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Object[] result;

        try{
            result = returnTransactionService
                    .getQuantityOfAllReturnedProductsAndRevenueByTimePeriod(
                            requestBody.getStartingDate(), requestBody.getEndDate());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/return-transactions/returned-products/types-related-quantity-and-revenue-by-time-period")
    public ResponseEntity<List<Object[]>> getTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(
            @RequestBody TimePeriodModel requestBody){

        if(requestBody == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<Object[]> result;

        try{
            result = returnTransactionService
                    .getAllTypesAndTheirReturnedQuantityAndRevenueByTimePeriod(
                            requestBody.getStartingDate(), requestBody.getEndDate());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
