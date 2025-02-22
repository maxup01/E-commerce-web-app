package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductIdAndPageImageIdModel {

    private UUID productId;
    private UUID pageImageId;
}
