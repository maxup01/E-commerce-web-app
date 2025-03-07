package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.repository.logistic.DeliveryProviderRepository;
import org.example.backend.dao.repository.transaction.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionDataService {

    private final DeliveryProviderRepository deliveryProviderRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public TransactionDataService(DeliveryProviderRepository deliveryProviderRepository
            , PaymentMethodRepository paymentMethodRepository) {

        this.deliveryProviderRepository = deliveryProviderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Transactional
    public List<String> getAllDeliveryProvidersNames() {
        return deliveryProviderRepository.findAllNames();
    }

    @Transactional
    public List<String> getAllPaymentMethodsNames() {
        return paymentMethodRepository.findAllPaymentMethodNames();
    }
}
