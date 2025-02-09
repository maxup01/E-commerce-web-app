package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, UUID> {

    @Query("SELECT SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct AS o")
    List<Object[]> getAllQuantityOfOrderedProductsAndRevenue();

    @Query("SELECT o.product.type, SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct AS o GROUP BY o.product.type")
    List<Object[]> getAllTypesAndTheirOrderedQuantityAndRevenue();

    @Query("SELECT o.product, SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct AS o" +
            " WHERE LOWER(o.product.name) LIKE %:phrase% GROUP BY o.product.name")
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByPhrase(@Param("phrase") String phrase);

    @Query("SELECT o.product, SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct AS o" +
            " WHERE o.product.type = :type GROUP BY o.product.name")
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByType(@Param("type") String type);

    //TODO test this method
    @Query("SELECT SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct AS o WHERE " +
            "o.orderTransaction.transactionDate >= :startingDate AND o.orderTransaction.transactionDate <= :endingDate")
    List<Object[]> getAllQuantityOfOrderedProductsAndRevenueByTimePeriod(@Param("startingDate") Date startingDate,
                                                                         @Param("endingDate") Date endingDate);

    //TODO add method related to time
}
