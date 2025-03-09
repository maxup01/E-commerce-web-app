package org.example.backend.controller;

import org.example.backend.dao.service.OrderTransactionService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.logistic.DeliveryProviderNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.exception.transaction.PaymentMethodNotFoundException;
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
public class OrderTransactionController {

    private final OrderTransactionService orderTransactionService;

    @Autowired
    public OrderTransactionController(OrderTransactionService orderTransactionService) {
        this.orderTransactionService = orderTransactionService;
    }

    @PostMapping("/order/create")
    public ResponseEntity<OrderTransactionModel> createNewOrderTransaction(
            @RequestBody OrderTransactionModel orderTransactionModel) {

        if(orderTransactionModel == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        OrderTransactionModel result;

        try{
            result = orderTransactionService.saveNewOrderTransaction(orderTransactionModel);
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DeliveryProviderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PaymentMethodNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/admin/order/update-status-by-id")
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

    @GetMapping("/order-by-id")
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

    @GetMapping("/orders-by-search")
    public ResponseEntity<List<OrderTransactionModel>> getOrderTransactionsBySearch(
            @RequestParam("startingDate") Date startingDate,
            @RequestParam("endingDate") Date endingDate,
            @RequestParam("paymentMethodName") String paymentMethodName,
            @RequestParam("deliveryProviderName") String deliveryProviderName,
            @RequestParam("userEmail") String userEmail,
            @RequestParam("forbiddenOrderTransactionIds") List<UUID> forbiddenOrderTransactionIds) {

        if(forbiddenOrderTransactionIds == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<OrderTransactionModel> result;

        if((startingDate != null) && (endingDate != null) && (paymentMethodName != null) &&
                (deliveryProviderName != null) && (userEmail != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
                                startingDate, endingDate, paymentMethodName,
                                deliveryProviderName, userEmail);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null) && (paymentMethodName != null) &&
                (deliveryProviderName != null)){

            try{
                result = orderTransactionService
                            .getOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderName(
                                startingDate, endingDate, paymentMethodName,
                                deliveryProviderName, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null) && (paymentMethodName != null) &&
                (userEmail != null)){

            try{
                result = orderTransactionService.getOrderTransactionsByTimePeriodAndPaymentMethodNameAndUserEmail(
                        startingDate, endingDate, paymentMethodName, userEmail);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null) && (deliveryProviderName != null) &&
        (userEmail != null)){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
                                startingDate, endingDate, deliveryProviderName, userEmail,
                                forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((paymentMethodName != null) && (deliveryProviderName != null)
                && (userEmail != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
                                paymentMethodName, deliveryProviderName, userEmail);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null) && (paymentMethodName != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndPaymentMethodName(
                                startingDate, endingDate, paymentMethodName, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (deliveryProviderName != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndDeliveryProviderName(
                                startingDate, endingDate, deliveryProviderName, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)
                && (userEmail != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriodAndUserEmail(
                                startingDate, endingDate, userEmail, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((paymentMethodName != null) && (deliveryProviderName != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodNameAndDeliveryProviderName(
                                paymentMethodName, deliveryProviderName, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((paymentMethodName != null) && (userEmail != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodNameAndUserEmail(
                                paymentMethodName, userEmail, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((deliveryProviderName != null) && (userEmail != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByDeliveryProviderNameAndUserEmail(
                                deliveryProviderName, userEmail, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((startingDate != null) && (endingDate != null)) {

            try{
                result = orderTransactionService
                        .getOrderTransactionsByTimePeriod(
                                startingDate, endingDate, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(paymentMethodName != null){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByPaymentMethodName(
                                paymentMethodName, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(deliveryProviderName != null){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByDeliveryProviderName(
                                deliveryProviderName, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(userEmail != null){

            try{
                result = orderTransactionService
                        .getOrderTransactionsByUserEmail(userEmail, forbiddenOrderTransactionIds);
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/orders-by-ids")
    public ResponseEntity<List<OrderTransactionModel>> getOrderTransactionsByIds(
            @RequestParam("ids") List<UUID> ids){

        List<OrderTransactionModel> orderTransactionModels;

        try{
            orderTransactionModels = orderTransactionService
                    .getOrderTransactionsByIdList(ids);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(orderTransactionModels);
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

    @GetMapping("/orders/ordered-products/all/quantity-and-revenue")
    public ResponseEntity<Object[]> getAllQuantityAndRevenueOfOrderedProducts(){

        List<Object[]> result = orderTransactionService.getAllQuantityOfOrderedProductsAndRevenue();

        return ResponseEntity.status(HttpStatus.OK).body(result.get(0));
    }

    @GetMapping("/orders/ordered-products/all/types-and-related-quantity-and-revenue")
    public ResponseEntity<List<Object[]>> getAllTypesAndTheirOrderedQuantityAndRevenue(){

        return ResponseEntity.status(HttpStatus.OK)
                .body(orderTransactionService.getAllTypesAndTheirOrderedQuantityAndRevenue());
    }

    @GetMapping("/orders/ordered-products/all/quantity-and-revenue-by-time-period")
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

    @GetMapping("/orders/ordered-products/all/types-quantity-and-revenue-by-time-period")
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

    @GetMapping("/orders/ordered-products/product-quantity-and-revenue-by-search")
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
