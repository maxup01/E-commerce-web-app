package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Query("SELECT p FROM PaymentMethod AS p WHERE LOWER(p.name) = LOWER(:name)")
    PaymentMethod findByName(@Param("name") String name);
}
