package org.example.backend.dao.entity.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.product.Product;

import java.util.UUID;

//Entity for storing product image on product page
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPageImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public ProductPageImage(byte[] image) {
        this.image = image;
    }

    public ProductPageImage(byte[] image, Product product) {
        this.image = image;
        this.product = product;
    }
}
