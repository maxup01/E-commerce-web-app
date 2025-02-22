package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductModelAndStock {

    private ProductModel product;
    private Long stock;
}
