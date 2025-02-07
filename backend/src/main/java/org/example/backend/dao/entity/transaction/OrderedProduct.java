package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.dao.entity.product.Product;

import java.util.List;

//Entity for storing ordered product and ordered quantity of it
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderedProduct extends TransactionProduct{

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "ordered_product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id")
    )
    private List<Product> products;

    //This is many-to-many relationship only because of mappedBy in OrderedProduct entity.
    //It cannot be more than one related return transaction to this entity!!!
    @ManyToMany(mappedBy = "orderedProducts", fetch = FetchType.EAGER)
    private List<OrderTransaction> orderTransaction;

    public OrderedProduct(Product product, Long quantity, Double pricePerUnit) {
        super(quantity, pricePerUnit);
        this.products = List.of(product);
    }
}
