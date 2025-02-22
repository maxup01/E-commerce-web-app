package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.dao.entity.image.ProductPageImage;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductModelAndPageImages {

    private ProductModel product;
    private List<byte[]> images;

    public static List<byte[]> imagesFromPageImages(List<ProductPageImage> pageImages){

        ArrayList<byte[]> images = new ArrayList<>();

        pageImages.forEach(page -> images.add(page.getImage()));

        return images;
    }
}
