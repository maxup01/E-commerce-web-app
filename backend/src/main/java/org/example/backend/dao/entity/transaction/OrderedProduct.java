package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.dao.entity.product.Product;

//Entity for storing ordered product and ordered quantity of it
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderedProduct extends TransactionProduct{

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "order_transaction_id", referencedColumnName = "id")
    private OrderTransaction orderTransaction;

    public OrderedProduct(Product product, Long quantity, Double pricePerUnit) {
        super(quantity, pricePerUnit);
        this.product = product;
    }
}
