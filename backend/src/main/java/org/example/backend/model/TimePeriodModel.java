package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class TimePeriodModel {

    private Date startingDate;
    private Date endDate;
}
