package org.example.backend.dao.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.entity.transaction.ReturnedProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String EANCode;

    @Column(nullable = false)
    private String name;

    //Type of product (clothing, etc.)
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String description;

    //Height you need to specify in cm unit
    @Column
    private Integer height;

    //Width you need to specify in cm unit
    @Column
    private Integer width;

    @Column(nullable = false)
    private Double regularPrice;

    @Column(nullable = false)
    private Double currentPrice;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Stock stock;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "main_image_id", referencedColumnName = "id")
    private ProductMainImage mainImage;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<ProductPageImage> pageImages;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private List<OrderedProduct> orderedProducts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private List<ReturnedProduct> returnedProducts;

    public Product(String name, String EANCode, String type, String description, Integer height, Integer width,
                   Double regularPrice, Double currentPrice, Stock stock, ProductMainImage mainImage) {
        this.EANCode = EANCode;
        this.name = name;
        this.type = type;
        this.description = description;
        this.height = height;
        this.width = width;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.stock = stock;
        this.mainImage = mainImage;
        this.pageImages = new ArrayList<>();
        this.orderedProducts = new ArrayList<>();
        this.returnedProducts = new ArrayList<>();
    }

    public Product(String name, String EANCode, String type, String description, Integer height, Integer width, Double regularPrice,
                   Double currentPrice, Stock stock, ProductMainImage mainImage, List<ProductPageImage> images) {
        this.EANCode = EANCode;
        this.name = name;
        this.type = type;
        this.description = description;
        this.height = height;
        this.width = width;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.stock = stock;
        this.mainImage = mainImage;
        this.pageImages = images;
        this.orderedProducts = new ArrayList<>();
        this.returnedProducts = new ArrayList<>();
    }
}
