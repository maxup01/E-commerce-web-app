package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.dao.entity.product.Product;

//Entity for storing data with returned product and it's quantity
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnedProduct extends TransactionProduct {

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "return_transaction_id", referencedColumnName = "id")
    private ReturnTransaction returnTransaction;

    public ReturnedProduct(Product product, Long quantity, Double pricePerUnit) {
        super(quantity, pricePerUnit);
        this.product = product;
    }
}
