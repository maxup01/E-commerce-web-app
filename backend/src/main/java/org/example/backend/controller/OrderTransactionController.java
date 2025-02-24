package org.example.backend.controller;

import org.example.backend.dao.service.OrderTransactionService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderTransactionController {

    private final OrderTransactionService orderTransactionService;

    @Autowired
    public OrderTransactionController(OrderTransactionService orderTransactionService) {
        this.orderTransactionService = orderTransactionService;
    }

    @PutMapping("/order/update-status-by-id")
    public ResponseEntity<OrderTransactionModel> updateOrderTransactionStatusByOrderTransactionId(
            @RequestBody TransactionIdAndTransactionStatusModel transactionIdAndTransactionStatusModel) {

        if(transactionIdAndTransactionStatusModel == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        OrderTransactionModel orderTransactionModel;

        try{
            orderTransactionModel = orderTransactionService
                    .updateOrderTransactionStatusById(
                            transactionIdAndTransactionStatusModel.getTransactionId(),
                            transactionIdAndTransactionStatusModel.getTransactionStatus());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (OrderTransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(orderTransactionModel);
    }

    @GetMapping("/order")
    public ResponseEntity<OrderTransactionModel> getOrderTransactionById(@RequestBody UUID orderId) {

        OrderTransactionModel orderTransactionModel;

        try{
            orderTransactionModel = orderTransactionService.getOrderTransactionById(orderId);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (OrderTransactionNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(orderTransactionModel);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderTransactionModel>> getOrderTransactions(
            @RequestBody OrderTransactionSearchModel ots) {

        List<OrderTransactionModel> result;

        if(ots == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getPaymentMethodName() != null) &&
                (ots.getDeliveryProviderName() != null) && (ots.getUserEmail() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
                                ots.getStartingDate(), ots.getEndingDate(), ots.getPaymentMethodName(),
                                ots.getDeliveryProviderName(), ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getPaymentMethodName() != null) &&
                (ots.getDeliveryProviderName() != null)){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderName(ots.getStartingDate(),
                                ots.getEndingDate(), ots.getPaymentMethodName(), ots.getDeliveryProviderName());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getPaymentMethodName() != null) &&
                (ots.getUserEmail() != null)){

            try{
                result = orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodNameAndUserEmail(
                        ots.getStartingDate(), ots.getEndingDate(), ots.getPaymentMethodName(), ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getDeliveryProviderName() != null) &&
        (ots.getUserEmail() != null)){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(ots.getStartingDate(),
                                ots.getEndingDate(), ots.getDeliveryProviderName(), ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getPaymentMethodName() != null) && (ots.getDeliveryProviderName() != null)
                && (ots.getUserEmail() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
                                ots.getPaymentMethodName(), ots.getDeliveryProviderName(), ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getPaymentMethodName() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndPaymentMethodName(ots.getStartingDate(), ots.getEndingDate(),
                                ots.getPaymentMethodName());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getDeliveryProviderName() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndDeliveryProviderName(ots.getStartingDate(), ots.getEndingDate(),
                                ots.getDeliveryProviderName());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null) && (ots.getUserEmail() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndUserEmail(ots.getStartingDate(), ots.getEndingDate(),
                                ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getPaymentMethodName() != null) && (ots.getDeliveryProviderName() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodNameAndDeliveryProviderName(ots.getPaymentMethodName(),
                                ots.getDeliveryProviderName());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getPaymentMethodName() != null) && (ots.getUserEmail() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodNameAndUserEmail(ots.getPaymentMethodName(), ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getDeliveryProviderName() != null) && (ots.getUserEmail() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByDeliveryProviderNameAndUserEmail(ots.getDeliveryProviderName(),
                                ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((ots.getStartingDate() != null) && (ots.getEndingDate() != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriod(ots.getStartingDate(), ots.getEndingDate());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(ots.getPaymentMethodName() != null){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodName(ots.getPaymentMethodName());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(ots.getDeliveryProviderName() != null){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByDeliveryProviderName(ots.getDeliveryProviderName());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(ots.getUserEmail() != null){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByUserEmail(ots.getUserEmail());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/orders/count-by-time-period")
    public ResponseEntity<Long> getCountOfAllOrderTransactionsByTimePeriod(
            @RequestBody TimePeriodModel timePeriodModel) {

        if(timePeriodModel == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Long count;

        try{
            count = orderTransactionService
                    .getCountOfAllOrderTransactionsByTimePeriod(
                            timePeriodModel.getStartingDate(), timePeriodModel.getEndDate());
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    @GetMapping("/orders/ordered-products/quantity-and-revenue")
    public ResponseEntity<Object[]> getAllQuantityAndRevenueOfOrderedProducts(){

        List<Object[]> result = orderTransactionService.getAllQuantityOfOrderedProductsAndRevenue();

        return ResponseEntity.status(HttpStatus.OK).body(result.get(0));
    }

    @GetMapping("/orders/ordered-products/types-quantity-and-revenue")
    public ResponseEntity<List<Object[]>> getAllTypesAndTheirOrderedQuantityAndRevenue(){

        return ResponseEntity.status(HttpStatus.OK)
                .body(orderTransactionService.getAllTypesAndTheirOrderedQuantityAndRevenue());
    }

    @GetMapping("/orders/ordered-products/quantity-and-revenue-by-time-period")
    public ResponseEntity<List<Object[]>> getQuantityOfOrderedProductsAndRevenueByTimePeriod(
            @RequestBody TimePeriodModel timePeriodModel){

        if(timePeriodModel == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<Object[]> result;

        try{
            result = orderTransactionService
                    .getQuantityOfOrderedProductsAndRevenueByTimePeriod(
                            timePeriodModel.getStartingDate(), timePeriodModel.getEndDate());
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/orders/ordered-products/product-types-quantity-and-revenue-by-time-period")
    public ResponseEntity<List<Object[]>> getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(
            @RequestBody TimePeriodModel timePeriodModel){

        if(timePeriodModel == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<Object[]> result;

        try{
            result = orderTransactionService
                    .getProductTypesAndTheirOrderedQuantityAndRevenueByTimePeriod(
                            timePeriodModel.getStartingDate(), timePeriodModel.getEndDate());
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/orders/ordered-products/product-quantity-and-revenue")
    public ResponseEntity<List<Object[]>> getProductsAndTheirOrderedQuantityAndRevenueBy(
            @RequestBody ProductAndQuantityAndRevenueSearchModel requestBody){

        List<Object[]> result;

        if(requestBody == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)
                && (requestBody.getType() != null) && (requestBody.getPhrase() != null)) {

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndTypeAndPhrase(
                                requestBody.getStartingDate(), requestBody.getStartingDate(), requestBody.getType(),
                                requestBody.getPhrase());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)
                && (requestBody.getType() != null)) {

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndType(
                                requestBody.getStartingDate(), requestBody.getEndingDate(), requestBody.getType());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)
                && (requestBody.getPhrase() != null)) {

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByTimePeriodAndPhrase(
                                requestBody.getStartingDate(), requestBody.getStartingDate(), requestBody.getPhrase());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getType() != null) && (requestBody.getPhrase() != null)){

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByTypeAndPhrase(
                                requestBody.getType(), requestBody.getPhrase());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((requestBody.getStartingDate() != null) && (requestBody.getEndingDate() != null)){

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByTimePeriod(
                                requestBody.getStartingDate(), requestBody.getEndingDate());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(requestBody.getType() != null){

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByType(requestBody.getType());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(requestBody.getPhrase() != null){

            try{
                result = orderTransactionService
                        .getProductsAndTheirOrderedQuantityAndRevenueByPhrase(requestBody.getPhrase());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
