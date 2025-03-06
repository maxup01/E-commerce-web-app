package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.product.Product;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModel {

    private String EANCode;
    private String name;
    private String type;
    private String description;
    private Integer height;
    private Integer width;
    private Double regularPrice;
    private Double currentPrice;
    private byte[] mainImage;

    @Override
    public boolean equals(Object o) {

        return (o instanceof ProductModel) && ((ProductModel) o).getEANCode().equals(this.getEANCode());
    }

    public static ProductModel fromProduct(Product product) {

        return new ProductModel(product.getEANCode(), product.getName(),
                product.getType(), product.getDescription(), product.getHeight(), product.getWidth(),
                product.getRegularPrice(), product.getCurrentPrice(), product.getMainImage().getImage());
    }
}
