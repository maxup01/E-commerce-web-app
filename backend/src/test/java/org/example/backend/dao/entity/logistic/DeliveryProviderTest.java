package org.example.backend.dao.entity.logistic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeliveryProviderTest {

    private final String RANDOM_DELIVERY_PROVIDER_NAME = "Credit card";
    private final boolean RANDOM_ENABLED_VALUE = true;

    @Test
    public void testOfConstructorWithNameAndEnabledArguments() {

        DeliveryProvider deliveryProvider = new DeliveryProvider(RANDOM_DELIVERY_PROVIDER_NAME, RANDOM_ENABLED_VALUE);

        assertNull(deliveryProvider.getId());
        assertEquals(deliveryProvider.getName(), RANDOM_DELIVERY_PROVIDER_NAME);
        assertEquals(deliveryProvider.isEnabled(), RANDOM_ENABLED_VALUE);
    }
}
