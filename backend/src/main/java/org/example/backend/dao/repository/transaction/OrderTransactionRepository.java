package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, UUID> {

    @Query("SELECT COUNT(o) FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate")
    Long getCountOfAllOrderTransactionsByTimePeriod(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate")
    List<OrderTransaction> findOrderTransactionByTimePeriod(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.paymentMethod.name = :paymentMethodName")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndPaymentMethodName(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
                                                                                 @Param("paymentMethodName") String paymentMethodName);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.deliveryProvider.name = :deliveryProviderName")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndDeliveryProviderName(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
                                                                                    @Param("deliveryProviderName") String deliveryProviderName);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.user.email = :userEmail")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndUserEmail(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
                                                                         @Param("userEmail") String userEmail);
}
