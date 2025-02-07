package org.example.backend.dao.repository.transaction;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.transaction.ReturnCause;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReturnCauseRepositoryTest {

    private final String RANDOM_CAUSE = "random cause";
    private final String THE_SAME_CAUSE_WITH_DIFFERENT_CASE = "RANDOM CAUSE";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReturnCauseRepository returnCauseRepository;

    @Test
    public void testOfSave(){

        ReturnCause returnCause1 = new ReturnCause(RANDOM_CAUSE);

        assertDoesNotThrow(() -> {
            returnCauseRepository.save(returnCause1);
            entityManager.flush();
        });

        ReturnCause returnCause2 = new ReturnCause(RANDOM_CAUSE);

        assertThrows(DataIntegrityViolationException.class, () -> {
            returnCauseRepository.save(returnCause2);
            entityManager.flush();
        });
    }

    @Test
    public void testOfFindByCause(){

        ReturnCause returnCause = new ReturnCause(RANDOM_CAUSE);
        returnCauseRepository.save(returnCause);

        ReturnCause foundReturnCause = returnCauseRepository.findByCause(THE_SAME_CAUSE_WITH_DIFFERENT_CASE);

        assertEquals(foundReturnCause.getCause(), RANDOM_CAUSE);
    }
}
