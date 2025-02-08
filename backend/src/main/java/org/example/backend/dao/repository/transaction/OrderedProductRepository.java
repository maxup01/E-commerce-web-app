package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, UUID> {

    @Query("SELECT SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct o")
    List<Object[]> getAllQuantityOfOrderedProductsAndRevenue();

    @Query("SELECT o.product.type, SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct o GROUP BY o.product.type")
    List<Object[]> getAllTypesAndTheirOrderedQuantityAndRevenue();

    @Query("SELECT o.product, SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct o" +
            " WHERE LOWER(o.product.name) LIKE LOWER(:phrase) GROUP BY o.product.name")
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByPhrase(@Param("phrase") String phrase);

    @Query("SELECT o.product, SUM(o.quantity), SUM(o.quantity * o.pricePerUnit) FROM OrderedProduct o" +
            " WHERE LOWER(o.product.type) = LOWER(:type) GROUP BY o.product.name")
    List<Object[]> getProductsAndTheirOrderedQuantityAndRevenueByType(@Param("type") String type);


}
