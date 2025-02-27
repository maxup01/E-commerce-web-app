package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.ReturnedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReturnedProductRepository extends JpaRepository<ReturnedProduct, UUID> {

    @Query("SELECT SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r")
    List<Object[]> getAllQuantityOfReturnedProductsAndRevenue();

    @Query("SELECT r.product.type, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r GROUP BY r.product.type")
    List<Object[]> getAllTypesAndTheirReturnedQuantityAndRevenue();

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate " +
            " GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriod(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE LOWER(r.product.name) LIKE %:phrase% GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByPhrase(@Param("phrase") String phrase);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.product.type = :type GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByType(@Param("type") String type);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.userEmail = :userEmail GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByUserEmail(
            @Param("userEmail") String userEmail);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate " +
            " AND LOWER(r.product.name) LIKE %:phrase% GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhrase(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("phrase") String phrase);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate " +
            " AND r.product.type = :type GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndType(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("type") String type);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate " +
            " AND r.returnTransaction.userEmail = :userEmail GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("userEmail") String userEmail);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE LOWER(r.product.name) LIKE %:phrase% AND r.product.type = :type GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndType(
            @Param("phrase") String phrase, @Param("type") String type);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE LOWER(r.product.name) LIKE %:phrase% AND r.returnTransaction.userEmail = :userEmail " +
            " GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByPhraseAndUserEmail(
            @Param("phrase") String phrase, @Param("userEmail") String userEmail);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.product.type = :type AND r.returnTransaction.userEmail = :userEmail " +
            " GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTypeAndUserEmail(
            @Param("type") String type, @Param("userEmail") String userEmail);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate " +
            " AND LOWER(r.product.name) LIKE %:phrase% AND r.product.type = :type " +
            " GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndType(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("phrase") String phrase, @Param("type") String type);

    @Query("SELECT r.product, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r" +
            " WHERE r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate " +
            " AND LOWER(r.product.name) LIKE %:phrase% AND r.returnTransaction.userEmail = :userEmail " +
            " GROUP BY r.product.name")
    List<Object[]> getProductsAndTheirReturnedQuantityAndRevenueByTimePeriodAndPhraseAndUserEmail(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("phrase") String phrase, @Param("userEmail") String userEmail);

    @Query("SELECT SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r WHERE " +
            "r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate")
    List<Object[]> getAllQuantityOfReturnedProductsAndRevenueByTimePeriod(@Param("startingDate") Date startingDate,
                                                                         @Param("endingDate") Date endingDate);

    @Query("SELECT r.product.type, SUM(r.quantity), SUM(r.quantity * r.pricePerUnit) FROM ReturnedProduct AS r WHERE " +
            "r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate GROUP BY r.product.type")
    List<Object[]> getAllTypesAndTheirQuantityOfReturnedProductsAndRevenueByTimePeriod(@Param("startingDate") Date startingDate,
                                                                                      @Param("endingDate") Date endingDate);

    @Query("SELECT r FROM ReturnedProduct AS r WHERE " +
            "r.returnTransaction.date >= :startingDate AND r.returnTransaction.date <= :endingDate AND " +
            "r.orderTransactionId = :transactionId")
    List<ReturnedProduct> getReturnedProductByTimePeriodAndTransactionId(
            @Param("startingDate") Date startingDate, @Param("endingDate") Date endingDate,
            @Param("transactionId") UUID transactionId);
}
