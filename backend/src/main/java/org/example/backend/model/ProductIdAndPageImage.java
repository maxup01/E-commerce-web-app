package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductIdAndPageImage {

    private UUID productId;
    private byte[] image;
}
