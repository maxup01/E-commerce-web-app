package org.example.backend.dao.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(nullable = false)
    private Double regularPrice;

    @Column(nullable = false)
    private Double currentPrice;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<Stock> stocks;

    public Product(String name, String EANCode, String type, String description, Double regularPrice, Double currentPrice, List<Stock> stocks) {
        this.EANCode = EANCode;
        this.name = name;
        this.type = type;
        this.description = description;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.stocks = stocks;
    }
}
