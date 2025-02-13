package org.example.backend.dao.repository.product;

import org.example.backend.dao.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product AS p WHERE p.type = :type")
    List<Product> findByType(@Param("type") String type);

    //You need to adjust percent signs at the beginning and at the end of phrase argument
    @Query("SELECT p FROM Product AS p WHERE LOWER(p.name) LIKE %:phrase%")
    List<Product> findByPhrase(@Param("phrase") String phrase);

    @Query("SELECT p FROM Product AS p WHERE LOWER(p.name) LIKE %:phrase% AND p.type = :type")
    List<Product> findByPhraseAndType(@Param("phrase") String phrase, @Param("type") String type);

    @Query("SELECT p FROM Product AS p WHERE :minimalPrice <= p.currentPrice AND :maximalPrice >= p.currentPrice")
    List<Product> findByPriceRange(@Param("minimalPrice") Double minimalPrice, @Param("maximalPrice") Double maximalPrice);

    //You need to adjust percent signs at the beginning and at the end of phrase argument
    @Query("SELECT p FROM Product AS p WHERE LOWER(p.name) LIKE %:phrase% AND p.type = :type" +
    " AND :minimalPrice <= p.currentPrice AND :maximalPrice >= p.currentPrice")
    List<Product> findByPhraseAndTypeAndPriceRanges(@Param("phrase") String phrase, @Param("type") String type,
                                                    @Param("minimalPrice") Double minimalPrice, @Param("maximalPrice") Double maximalPrice);

    @Query("SELECT p FROM Product AS p WHERE p.currentPrice != p.regularPrice")
    List<Product> showOnSale();

    @Query("SELECT SUM(p.stock.quantity) FROM Product AS p")
    Long getTotalQuantityOfProducts();

    @Query("SELECT p.type, SUM(p.stock.quantity) FROM Product AS p GROUP BY p.type")
    List<Object[]> getTypesAndQuantityOfProductsWithThisTypes();

    //You need to adjust percent signs at the beginning and at the end
    @Query("SELECT p, SUM(p.stock.quantity) FROM Product AS p WHERE LOWER(p.name) LIKE %:phrase% GROUP BY p.name")
    List<Object[]> getProductsAndRelatedQuantityByPhrase(@Param("phrase") String phrase);
}
