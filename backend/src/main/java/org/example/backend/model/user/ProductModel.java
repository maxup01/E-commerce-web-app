package org.example.backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModel {

    private UUID id;
    private String EANCode;
    private String name;
    private String type;
    private String description;
    private Integer height;
    private Integer width;
    private Double regularPrice;
    private Double currentPrice;
    private byte[] mainImage;
}
