package org.example.backend.dao.repository.logistic;

import org.example.backend.dao.entity.logistic.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class AddressJpaRepositoryTest {

    private final String RANDOM_COUNTRY_NAME_UPPER_CASE = "POLAND";
    private final String RANDOM_PROVINCE_NAME_UPPER_CASE = "MAZOWIECKIE";
    private final String RANDOM_CITY_NAME_UPPER_CASE = "WARSAW";
    private final String RANDOM_ADDRESS_UPPER_CASE = "XYZ";
    private final String RANDOM_COUNTRY_NAME_LOWER_CASE = "poland";
    private final String RANDOM_PROVINCE_NAME_LOWER_CASE = "mazowieckie";
    private final String RANDOM_CITY_NAME_LOWER_CASE = "warsaw";
    private final String RANDOM_ADDRESS_LOWER_CASE = "xyz";

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void testOfSave(){

        Address address = new Address(RANDOM_COUNTRY_NAME_UPPER_CASE, RANDOM_PROVINCE_NAME_UPPER_CASE,
                RANDOM_CITY_NAME_UPPER_CASE, RANDOM_ADDRESS_UPPER_CASE);

        assertDoesNotThrow(() -> {
            addressRepository.save(address);
        });
    }

    @Test
    public void testOfFindAddressByCountryContainingIgnoreCaseAndProvinceContainingIgnoreCaseAndCityContainingIgnoreCaseAndAddressContainingIgnoreCase(){

        Address address = new Address(RANDOM_COUNTRY_NAME_UPPER_CASE, RANDOM_PROVINCE_NAME_UPPER_CASE,
                RANDOM_CITY_NAME_UPPER_CASE, RANDOM_ADDRESS_UPPER_CASE);
        addressRepository.save(address);

        Address address1 = addressRepository.findByCountryAndCityAndProvinceAndAddress(RANDOM_COUNTRY_NAME_LOWER_CASE,
                RANDOM_PROVINCE_NAME_LOWER_CASE, RANDOM_CITY_NAME_LOWER_CASE, RANDOM_ADDRESS_LOWER_CASE);

        Address address2 = addressRepository.findByCountryAndCityAndProvinceAndAddress(RANDOM_COUNTRY_NAME_UPPER_CASE,
                RANDOM_PROVINCE_NAME_UPPER_CASE, RANDOM_CITY_NAME_UPPER_CASE, RANDOM_ADDRESS_UPPER_CASE);

        assertEquals(address1.getId(), address2.getId());
        assertEquals(address1.getCountry(), address2.getCountry());
        assertEquals(address1.getCity(), address2.getCity());
        assertEquals(address1.getProvince(), address2.getProvince());
        assertEquals(address1.getAddress(), address2.getAddress());
        assertEquals(address1.getOrderTransactions(), address2.getOrderTransactions());
        assertEquals(address1.getReturnTransactions(), address2.getReturnTransactions());
    }
}
