package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.dao.entity.image.ProductPageImage;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductPageImageModel {

    private UUID id;
    private byte[] image;

    public static ProductPageImageModel fromProductPageImage(ProductPageImage productPageImage) {
        return new ProductPageImageModel(productPageImage.getId(), productPageImage.getImage());
    }
}
