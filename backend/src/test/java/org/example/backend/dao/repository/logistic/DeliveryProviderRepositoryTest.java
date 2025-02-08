package org.example.backend.dao.repository.logistic;

import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DeliveryProviderRepositoryTest {

    final private String RANDOM_DELIVERY_NAME_LOWER_CASE = "random delivery provider name";
    final private String DIFFERENT_DELIVERY_NAME = "Different delivery provider name";
    final private String ANOTHER_RANDOM_DELIVERY_NAME = "Another random delivery provider name";
    final private boolean ENABLED_TRUE = true;
    final private boolean ENABLED_FALSE = false;

    @Autowired
    DeliveryProviderRepository deliveryProviderRepository;

    @Test
    public void testOfSave(){

        DeliveryProvider provider1 = new DeliveryProvider(RANDOM_DELIVERY_NAME_LOWER_CASE, ENABLED_TRUE);
        DeliveryProvider provider2 = new DeliveryProvider(RANDOM_DELIVERY_NAME_LOWER_CASE, ENABLED_FALSE);

        assertDoesNotThrow(() -> {
            deliveryProviderRepository.save(provider1);
        });

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            deliveryProviderRepository.save(provider2);
        });
    }

    @Test
    public void testOfFindByName(){

        DeliveryProvider provider = new DeliveryProvider(RANDOM_DELIVERY_NAME_LOWER_CASE, ENABLED_TRUE);
        deliveryProviderRepository.save(provider);

        DeliveryProvider foundDeliveryProvider1 = deliveryProviderRepository.findByName(RANDOM_DELIVERY_NAME_LOWER_CASE);

        assertNotNull(foundDeliveryProvider1.getId());
        assertEquals(foundDeliveryProvider1.getName(), RANDOM_DELIVERY_NAME_LOWER_CASE);
        assertEquals(foundDeliveryProvider1.isEnabled(), ENABLED_TRUE);
    }

    @Test
    public void testOffFindByEnabledTrue(){

        DeliveryProvider provider1 = new DeliveryProvider(RANDOM_DELIVERY_NAME_LOWER_CASE, ENABLED_TRUE);
        deliveryProviderRepository.save(provider1);

        DeliveryProvider provider2 = new DeliveryProvider(DIFFERENT_DELIVERY_NAME, ENABLED_FALSE);
        deliveryProviderRepository.save(provider2);

        DeliveryProvider provider3 = new DeliveryProvider(ANOTHER_RANDOM_DELIVERY_NAME, ENABLED_TRUE);
        deliveryProviderRepository.save(provider3);

        List<DeliveryProvider> providers = deliveryProviderRepository.findAllByEnabledTrue();

        assertEquals(providers.size(), 2);
    }

    @Test
    public void testOffFindByEnabledFalse(){

        DeliveryProvider provider1 = new DeliveryProvider(RANDOM_DELIVERY_NAME_LOWER_CASE, ENABLED_TRUE);
        deliveryProviderRepository.save(provider1);

        DeliveryProvider provider2 = new DeliveryProvider(DIFFERENT_DELIVERY_NAME, ENABLED_FALSE);
        deliveryProviderRepository.save(provider2);

        DeliveryProvider provider3 = new DeliveryProvider(ANOTHER_RANDOM_DELIVERY_NAME, ENABLED_TRUE);
        deliveryProviderRepository.save(provider3);

        List<DeliveryProvider> providers = deliveryProviderRepository.findAllByEnabledFalse();

        assertEquals(providers.size(), 1);
    }
}
