package org.example.backend.model;

import lombok.Getter;

import java.util.Date;

@Getter
public class ProductAndQuantityAndRevenueSearchModel {

    private Date startingDate;
    private Date endingDate;
    private String type;
    private String phrase;

    public ProductAndQuantityAndRevenueSearchModel(Date startingDate, Date endingDate,
                                                   String type, String phrase) {
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.type = type;
        this.phrase = phrase.toLowerCase();
    }
}
