package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.transaction.ReturnCause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReturnCauseRepository extends JpaRepository<ReturnCause, Long> {

    @Query("SELECT r FROM ReturnCause AS r WHERE r.cause = :cause")
    ReturnCause findByCause(@Param("cause") String cause);
}
