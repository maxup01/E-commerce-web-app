package org.example.backend.controller;

import org.example.backend.dao.service.OrderTransactionService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.transaction.OrderTransactionNotFoundException;
import org.example.backend.model.OrderTransactionModel;
import org.example.backend.model.TimePeriodModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
            @RequestBody TimePeriodModel timePeriodModel) {

        //TODO make this function body to looks like body in getProductsByProductSearchModel in ProductController
        return null;
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
}
