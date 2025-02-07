package org.example.backend.dao.repository.transaction;

import jakarta.persistence.EntityManager;
import org.example.backend.dao.entity.transaction.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PaymentMethodRepositoryTest {

    private final String RANDOM_PAYMENT_METHOD_NAME = "Random payment method name";
    private final String THE_SAME_PAYMENT_METHOD_NAME_WITH_RANDOM_CASE = "RANDOM PAYMENT METHOD NAME";
    private final boolean RANDOM_ENABLED_VALUE = true;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    public void testOfSave(){

        PaymentMethod paymentMethod1 = new PaymentMethod(RANDOM_PAYMENT_METHOD_NAME, RANDOM_ENABLED_VALUE);

        assertDoesNotThrow(() -> {
            paymentMethodRepository.save(paymentMethod1);
            entityManager.flush();
        });

        PaymentMethod paymentMethod2 = new PaymentMethod(RANDOM_PAYMENT_METHOD_NAME, RANDOM_ENABLED_VALUE);

        //This should throw this error because paymentMethod2 has the same name as paymentDelivery1
        assertThrows(DataIntegrityViolationException.class, () -> {
            paymentMethodRepository.save(paymentMethod2);
            entityManager.flush();
        });
    }

    @Test
    public void testOfFindByName(){

        PaymentMethod paymentMethod = new PaymentMethod(RANDOM_PAYMENT_METHOD_NAME, RANDOM_ENABLED_VALUE);
        paymentMethodRepository.save(paymentMethod);

        PaymentMethod foundPaymentMethod = paymentMethodRepository.findByName(THE_SAME_PAYMENT_METHOD_NAME_WITH_RANDOM_CASE);

        assertEquals(foundPaymentMethod.getName(), RANDOM_PAYMENT_METHOD_NAME);
    }
}
