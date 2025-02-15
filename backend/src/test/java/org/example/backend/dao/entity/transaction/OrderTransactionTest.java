package org.example.backend.dao.entity.transaction;

import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.user.User;
import org.example.backend.enumerated.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderTransactionTest {

    private final Date RANDOM_TRANSACTION_DATE = new Date();
    private final User RANDOM_USER = new User();
    private final Address RANDOM_DELIVERY_ADDRESS = new Address();
    private final DeliveryProvider RANDOM_DELIVERY_PROVIDER = new DeliveryProvider();
    private final PaymentMethod RANDOM_PAYMENT_METHOD = new PaymentMethod();
    private final ArrayList<OrderedProduct> RANDOM_ORDERED_PRODUCT_LIST = new ArrayList<>();

    private final Long RANDOM_QUANTITY = 1L;
    private final Double RANDOM_UNIT_PRICE = 100.00;

    @Test
    public void testOfConstructorWithTransactionDateAndUserAndDeliveryAddressAndDeliveryProviderAndPaymentMethodAndOrderedProductListArguments() {

        RANDOM_ORDERED_PRODUCT_LIST.add(new OrderedProduct(new Product(), RANDOM_QUANTITY, RANDOM_UNIT_PRICE));
        RANDOM_ORDERED_PRODUCT_LIST.add(new OrderedProduct(new Product(), RANDOM_QUANTITY, RANDOM_UNIT_PRICE));

        OrderTransaction orderTransaction = new OrderTransaction(RANDOM_TRANSACTION_DATE, RANDOM_USER, RANDOM_DELIVERY_ADDRESS, RANDOM_DELIVERY_PROVIDER,
                RANDOM_PAYMENT_METHOD, RANDOM_ORDERED_PRODUCT_LIST);

        assertNull(orderTransaction.getId());
        assertEquals(orderTransaction.getDate(), RANDOM_TRANSACTION_DATE);
        assertEquals(orderTransaction.getUser(), RANDOM_USER);
        assertEquals(orderTransaction.getDeliveryAddress(), RANDOM_DELIVERY_ADDRESS);
        assertEquals(orderTransaction.getDeliveryProvider(), RANDOM_DELIVERY_PROVIDER);
        assertEquals(orderTransaction.getPaymentMethod(), RANDOM_PAYMENT_METHOD);
        assertEquals(orderTransaction.getOrderedProducts(), RANDOM_ORDERED_PRODUCT_LIST);
        assertEquals(orderTransaction.getStatus(), TransactionStatus.PAID);
    }
}
