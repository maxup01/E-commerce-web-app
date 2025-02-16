package org.example.backend.dao.entity.transaction;

import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.user.User;
import org.example.backend.enumerated.ReturnCause;
import org.example.backend.enumerated.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReturnTransactionTest {

    private final Date RANDOM_TRANSACTION_DATE = new Date();
    private final User RANDOM_USER = new User();
    private final Address RANDOM_DELIVERY_ADDRESS = new Address();
    private final DeliveryProvider RANDOM_DELIVERY_PROVIDER = new DeliveryProvider();
    private final ReturnCause RANDOM_RETURN_CAUSE = ReturnCause.CHANGED_MIND;
    private final Long RANDOM_QUANTITY = 1L;
    private final Double RANDOM_UNIT_PRICE = 100.00;
    private final UUID RANDOM_ORDER_TRANSACTION_ID = UUID.randomUUID();
    private final List<ReturnedProduct> RANDOM_RETRUNED_PRODUCT_LIST =
            List.of(new ReturnedProduct(new Product(), RANDOM_QUANTITY, RANDOM_UNIT_PRICE, RANDOM_ORDER_TRANSACTION_ID),
                    new ReturnedProduct(new Product(), RANDOM_QUANTITY, RANDOM_UNIT_PRICE, RANDOM_ORDER_TRANSACTION_ID));

    @Test
    public void testOfConstructorWithTransactionDateAndUserAndDeliveryAddressAndDeliveryProviderAndPaymentMethodAndOrderedProductListArguments() {

        ReturnTransaction returnTransaction = new ReturnTransaction(RANDOM_TRANSACTION_DATE, RANDOM_USER, RANDOM_DELIVERY_ADDRESS, RANDOM_DELIVERY_PROVIDER,
                RANDOM_RETURN_CAUSE, RANDOM_RETRUNED_PRODUCT_LIST);

        assertNull(returnTransaction.getId());
        assertEquals(returnTransaction.getDate(), RANDOM_TRANSACTION_DATE);
        assertEquals(returnTransaction.getUser(), RANDOM_USER);
        assertEquals(returnTransaction.getDeliveryAddress(), RANDOM_DELIVERY_ADDRESS);
        assertEquals(returnTransaction.getDeliveryProvider(), RANDOM_DELIVERY_PROVIDER);
        assertEquals(returnTransaction.getReturnCause(), RANDOM_RETURN_CAUSE);
        assertEquals(returnTransaction.getReturnedProducts(), RANDOM_RETRUNED_PRODUCT_LIST);
        assertEquals(returnTransaction.getStatus(), TransactionStatus.ACCEPTED_RETURN);
    }
}
