package org.example.backend.validator;

import org.example.backend.exception.global.BadArgumentException;

import java.util.Date;

public class DateValidator {

    public static void checkIfDatesAreGood(Date startingDate, Date endingDate) {
        if(startingDate == null)
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if(endingDate == null)
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");
    }
}
