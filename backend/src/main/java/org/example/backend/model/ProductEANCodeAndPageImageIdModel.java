package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductEANCodeAndPageImageIdModel {

    private String eanCode;
    private UUID pageImageId;
}
