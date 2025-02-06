package org.example.backend.dao.entity.logistic;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AddressTest {

    private final String RANDOM_COUNTRY_NAME = "Poland";
    private final String RANDOM_PROVINCE_NAME = "Mazowieckie";
    private final String RANDOM_CITY = "Warsaw";
    private final String RANDOM_ADDRESS = "XYZ 17/A";

    @Test
    public void testOfConstructorWithAllArgumentsExceptOfId() {

        Address address = new Address(RANDOM_COUNTRY_NAME, RANDOM_PROVINCE_NAME, RANDOM_CITY, RANDOM_ADDRESS);

        assertNull(address.getId());
        assertEquals(address.getCountry(), RANDOM_COUNTRY_NAME);
        assertEquals(address.getProvince(), RANDOM_PROVINCE_NAME);
        assertEquals(address.getCity(), RANDOM_CITY);
        assertEquals(address.getAddress(), RANDOM_ADDRESS);
    }
}
