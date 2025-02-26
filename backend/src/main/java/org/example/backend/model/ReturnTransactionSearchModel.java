package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.enumerated.ReturnCause;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ReturnTransactionSearchModel {

    private Date startingDate;
    private Date endingDate;
    private ReturnCause returnCause;
    private String deliveryProviderName;
    private String userEmail;
}
