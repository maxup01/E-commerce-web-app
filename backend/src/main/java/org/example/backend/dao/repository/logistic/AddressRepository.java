package org.example.backend.dao.repository.logistic;

import org.example.backend.dao.entity.logistic.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Long> {

    //Finds address instance by country, province, city and address case-insensitive
    @Query("SELECT a FROM Address AS a WHERE a.country = :country AND a.province = :province" +
            " AND a.city = :city AND a.address = :address")
    Address findByCountryAndCityAndProvinceAndAddress(@Param("country") String country, @Param("province") String province,
                                                      @Param("city") String city, @Param("address") String address);
}
