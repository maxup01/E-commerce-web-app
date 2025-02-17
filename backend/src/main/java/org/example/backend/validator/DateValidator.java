package org.example.backend.validator;

import org.example.backend.exception.global.BadArgumentException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateValidator {

    public static void checkIfDatesAreGood(Date startingDate, Date endingDate) {
        if((startingDate == null) || (!startingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: startingDate");
        else if((endingDate == null) || (!endingDate.before(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))))
            throw new BadArgumentException("Incorrect argument: endingDate");
        else if(startingDate.after(endingDate))
            throw new BadArgumentException("Argument startingDate is after endingDate");
    }
}
