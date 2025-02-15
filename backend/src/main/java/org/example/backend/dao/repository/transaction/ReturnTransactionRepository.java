package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.ReturnTransaction;
import org.example.backend.enumerated.ReturnCause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReturnTransactionRepository extends JpaRepository<ReturnTransaction, UUID> {

    @Query("SELECT COUNT(r) FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate")
    Long getCountOfAllReturnTransactionsByTimePeriod(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate")
    List<ReturnTransaction> findProductsByTimePeriod(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.returnCause = :returnCause")
    List<ReturnTransaction> findProductsByTimePeriodAndReturnCauseName(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
                                                                        @Param("returnCause") ReturnCause returnCause);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.deliveryProvider.name = :deliveryProviderName")
    List<ReturnTransaction> findProductsByTimePeriodAndDeliveryProviderName(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
                                                                           @Param("deliveryProviderName") String deliveryProviderName);

    @Query("SELECT r FROM ReturnTransaction AS r WHERE r.date >= :startingDate AND r.date <= :endingDate AND " +
            " r.user.email = :userEmail")
    List<ReturnTransaction> findProductsByTimePeriodAndUserEmail(@Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
                                                                @Param("userEmail") String userEmail);
}
