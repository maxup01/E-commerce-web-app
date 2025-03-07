package org.example.backend.dao.repository.logistic;

import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryProviderRepository extends JpaRepository<DeliveryProvider, Long> {

    List<DeliveryProvider> findAllByEnabledFalse();

    List<DeliveryProvider> findAllByEnabledTrue();

    @Query("SELECT d FROM DeliveryProvider AS d WHERE d.name = :name")
    DeliveryProvider findByName(@Param("name") String name);

    @Query("SELECT d.name FROM DeliveryProvider AS d GROUP BY d.name")
    List<String> findAllNames();
}
