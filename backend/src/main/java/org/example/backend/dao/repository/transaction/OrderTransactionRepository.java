package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, UUID> {

    @Query("SELECT COUNT(o) FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate")
    Long getCountOfAllOrderTransactionsByTimePeriod(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate " +
            " AND o.id NOT IN (:forbiddenOrderTransactionIds)")
    List<OrderTransaction> findOrderTransactionsByTimePeriod(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("forbiddenOrderTransactionIds") List<UUID> ids, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.paymentMethod.name = :paymentMethodName")
    List<OrderTransaction> findOrderTransactionsByPaymentMethodName(
            @Param("paymentMethodName") String paymentMethodName, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.deliveryProvider.name = :deliveryProviderName")
    List<OrderTransaction> findOrderTransactionsByDeliveryProviderName(
            @Param("deliveryProviderName") String deliveryProviderName, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.userEmail = :email")
    List<OrderTransaction> findOrderTransactionsByUserEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.paymentMethod.name = :paymentMethodName")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndPaymentMethodName(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("paymentMethodName") String paymentMethodName, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.deliveryProvider.name = :deliveryProviderName")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndDeliveryProviderName(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("deliveryProviderName") String deliveryProviderName, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.user.email = :userEmail")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("userEmail") String userEmail, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.paymentMethod.name = :paymentMethodName " +
            "AND o.deliveryProvider.name = :deliveryProviderName")
    List<OrderTransaction> findOrderTransactionsByPaymentMethodNameAndDeliveryProviderName(
            @Param("paymentMethodName") String paymentMethodName,
            @Param("deliveryProviderName") String deliveryProviderName, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.paymentMethod.name = :paymentMethodName AND " +
            "o.userEmail = :userEmail")
    List<OrderTransaction> findOrderTransactionsByPaymentMethodNameAndUserEmail(
            @Param("paymentMethodName") String paymentMethodName, @Param("userEmail") String userEmail,
            Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.deliveryProvider.name = :deliveryProviderName AND " +
            "o.userEmail = :userEmail")
    List<OrderTransaction> findOrderTransactionsByDeliveryProviderNameAndUserEmail(
            @Param("deliveryProviderName") String deliveryProviderName, @Param("userEmail") String userEmail,
            Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            "o.paymentMethod.name = :paymentMethodName AND o.deliveryProvider.name = :deliveryProviderName")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderName(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("paymentMethodName") String paymentMethodName,
            @Param("deliveryProviderName") String deliveryProviderName, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.deliveryProvider.name = :deliveryProviderName AND o.userEmail = :userEmail")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("userEmail") String userEmail, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.paymentMethod.name = :paymentMethodName AND o.userEmail = :userEmail")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndPaymentMethodNameAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("paymentMethodName") String paymentMethodName,
            @Param("userEmail") String userEmail, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.paymentMethod.name = :paymentMethodName AND " +
            " o.deliveryProvider.name = :deliveryProviderName AND o.userEmail = :userEmail")
    List<OrderTransaction> findOrderTransactionsByPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
            @Param("paymentMethodName") String paymentMethodName,
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("userEmail") String userEmail, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.date >= :startingDate AND o.date <= :endingDate AND " +
            " o.paymentMethod.name = :paymentMethodName AND " +
            " o.deliveryProvider.name = :deliveryProviderName AND o.userEmail = :userEmail")
    List<OrderTransaction> findOrderTransactionsByTimePeriodAndPaymentMethodNameAndDeliveryProviderNameAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("paymentMethodName") String paymentMethodName,
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("userEmail") String userEmail, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction AS o WHERE o.id IN (:ids)")
    List<OrderTransaction> findOrderTransactionsByIdList(@Param("ids") List<UUID> ids, Pageable pageable);
}
