package org.example.backend.dao.entity.logistic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PaymentMethodTest {

    private final String RANDOM_PAYMENT_METHOD_NAME = "Credit card";
    private final boolean RANDOM_ENABLED_VALUE = true;

    @Test
    public void testOfConstructorWithNameArgument(){

        PaymentMethod paymentMethod = new PaymentMethod(RANDOM_PAYMENT_METHOD_NAME, RANDOM_ENABLED_VALUE);

        assertNull(paymentMethod.getId());
        assertEquals(paymentMethod.getName(), RANDOM_PAYMENT_METHOD_NAME);
        assertEquals(paymentMethod.isEnabled(), RANDOM_ENABLED_VALUE);
    }
}
