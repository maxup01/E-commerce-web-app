package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionDataService {

    private final DeliveryProviderRepository deliveryProviderRepository;

    public TransactionDataService(DeliveryProviderRepository deliveryProviderRepository) {
        this.deliveryProviderRepository = deliveryProviderRepository;
    }

    @Transactional
    public List<String> getAllDeliveryProvidersNames() {
        return deliveryProviderRepository.findAllNames();
    }
}
