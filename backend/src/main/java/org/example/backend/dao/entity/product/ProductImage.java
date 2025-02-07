package org.example.backend.dao.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

//Entity for storing product image
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @Column(nullable = false)
    private boolean isMainImage;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public ProductImage(byte[] image) {
        this.image = image;
    }

    public ProductImage(byte[] image, boolean isMainImage, Product product) {
        this.image = image;
        this.isMainImage = isMainImage;
        this.product = product;
    }
}
