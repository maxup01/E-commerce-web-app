package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSearchModel {

    private String phrase;
    private String type;
    private Double minPrice;
    private Double maxPrice;
}
