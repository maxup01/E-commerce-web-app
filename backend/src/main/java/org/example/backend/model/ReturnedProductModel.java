package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnedProductModel {

    private UUID id;
    private ProductModel product;
    private Long quantity;
    private UUID transactionInWhichThisProductWasOrdered;
}
