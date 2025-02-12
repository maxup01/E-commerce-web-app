package org.example.backend.dao.entity.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.product.Product;

import java.util.UUID;

//Entity storing main product image (profile product image)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMainImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "mainImage")
    private Product product;

    public ProductMainImage(byte[] image) {
        this.image = image;
    }

    public ProductMainImage(byte[] image, Product product) {
        this.image = image;
        this.product = product;
    }
}
