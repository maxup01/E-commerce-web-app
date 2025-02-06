package org.example.backend.dao.entity.transaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReturnCauseTest {

    private final String RANDOM_CAUSE = "Broken";

    @Test
    public void testOfConstructorWithCauseArgument(){

        ReturnCause returnCause = new ReturnCause(RANDOM_CAUSE);

        assertNull(returnCause.getId());
        assertEquals(returnCause.getCause(), RANDOM_CAUSE);
    }
}
