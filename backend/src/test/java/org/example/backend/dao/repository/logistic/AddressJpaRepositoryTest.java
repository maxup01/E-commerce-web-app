package org.example.backend.dao.repository.logistic;

import org.example.backend.dao.entity.logistic.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AddressJpaRepositoryTest {

    private final String RANDOM_COUNTRY_NAME_LOWER_CASE = "poland";
    private final String RANDOM_PROVINCE_NAME_LOWER_CASE = "mazowieckie";
    private final String RANDOM_CITY_NAME_LOWER_CASE = "warsaw";
    private final String RANDOM_ADDRESS_LOWER_CASE = "xyz";

    private Address address;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        address = new Address(RANDOM_COUNTRY_NAME_LOWER_CASE, RANDOM_PROVINCE_NAME_LOWER_CASE,
                RANDOM_CITY_NAME_LOWER_CASE, RANDOM_ADDRESS_LOWER_CASE);
    }

    @Test
    public void testOfSave(){

        assertDoesNotThrow(() -> {
            addressRepository.save(address);
        });
    }

    @Test
    public void testOfFindByCountryAndCityAndProvinceAndAddress(){

        addressRepository.save(address);

        Address address1 = addressRepository.findByCountryAndCityAndProvinceAndAddress(RANDOM_COUNTRY_NAME_LOWER_CASE,
                RANDOM_PROVINCE_NAME_LOWER_CASE, RANDOM_CITY_NAME_LOWER_CASE, RANDOM_ADDRESS_LOWER_CASE);

        assertNotNull(address1.getId());
        assertEquals(address1.getCountry(), RANDOM_COUNTRY_NAME_LOWER_CASE);
        assertEquals(address1.getCity(), RANDOM_CITY_NAME_LOWER_CASE);
        assertEquals(address1.getProvince(), RANDOM_PROVINCE_NAME_LOWER_CASE);
        assertEquals(address1.getAddress(), RANDOM_ADDRESS_LOWER_CASE);
    }
}
