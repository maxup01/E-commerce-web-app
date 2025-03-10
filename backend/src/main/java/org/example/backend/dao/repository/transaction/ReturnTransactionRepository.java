package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.enumerated.ReturnCause;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReturnTransactionRepository extends JpaRepository<ReturnTransaction, UUID> {

    @Query("SELECT COUNT(r) FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate")
    Long getCountOfAllReturnTransactionsByTimePeriod(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate " +
            " AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriod(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.returnCause = :returnCause " +
            " AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByReturnCause(
            @Param("returnCause") ReturnCause returnCause,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.deliveryProvider.name = :deliveryProviderName " +
            " AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByDeliveryProviderName(
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.userEmail = :userEmail " +
            " AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByUserEmail(
            @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.returnCause = :returnCause AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndReturnCause(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("returnCause") ReturnCause returnCause,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.deliveryProvider.name = :deliveryProviderName AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndDeliveryProviderName(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.user.email = :userEmail AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.returnCause = :returnCause AND " +
            " r.deliveryProvider.name = :deliveryProviderName AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByReturnCauseAndDeliveryProviderName(
            @Param("returnCause") ReturnCause returnCause,
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.returnCause = :returnCause AND r.userEmail = :userEmail " +
            " AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByReturnCauseAndUserEmail(
            @Param("returnCause") ReturnCause returnCause, @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.deliveryProvider.name = :deliveryProviderName AND " +
            " r.userEmail = :userEmail AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByDeliveryProviderNameAndUserEmail(
            @Param("deliveryProviderName") String deliveryProviderName, @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.returnCause = :returnCause AND r.deliveryProvider.name = :deliveryProviderName " +
            " AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderName(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("returnCause") ReturnCause returnCause,
            @Param("deliveryProviderName") String deliveryProviderName,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.returnCause = :returnCause AND r.userEmail = :userEmail AND " +
            " r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndReturnCauseAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("returnCause") ReturnCause returnCause, @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.deliveryProvider.name = :deliveryProviderName AND r.userEmail = :userEmail AND " +
            " r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndDeliveryProviderNameAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("deliveryProviderName") String deliveryProviderName, @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.returnCause = :returnCause AND " +
            " r.deliveryProvider.name = :deliveryProviderName AND r.userEmail = :userEmail AND " +
            " r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByReturnCauseAndDeliveryProviderNameAndUserEmail(
            @Param("returnCause") ReturnCause returnCause,
            @Param("deliveryProviderName") String deliveryProviderName, @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.returnCause = :returnCause AND r.deliveryProvider.name = :deliveryProviderName AND " +
            " r.userEmail = :userEmail AND r.id NOT IN (:forbiddenReturnTransactionIds)")
    List<ReturnTransaction> findReturnTransactionsByTimePeriodAndReturnCauseAndDeliveryProviderNameAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("returnCause") ReturnCause returnCause,
            @Param("deliveryProviderName") String deliveryProviderName, @Param("userEmail") String userEmail,
            @Param("forbiddenReturnTransactionIds") List<UUID> forbiddenReturnTransactionIds,
            Pageable pageable);
}
