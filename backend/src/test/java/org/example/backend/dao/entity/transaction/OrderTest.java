package org.example.backend.dao.entity.transaction;

import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.logistic.PaymentMethod;
import org.example.backend.dao.entity.user.User;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderTest {

    private final Date RANDOM_TRANSACTION_DATE = new Date();
    private final User RANDOM_USER = new User();
    private final Address RANDOM_DELIVERY_ADDRESS = new Address();
    private final DeliveryProvider RANDOM_DELIVERY_PROVIDER = new DeliveryProvider();
    private final PaymentMethod RANDOM_PAYMENT_METHOD = new PaymentMethod();
    private final List<OrderedProduct> RANDOM_ORDERED_PRODUCT_LIST = List.of(new OrderedProduct(), new OrderedProduct());

    @Test
    public void testOfConstructorWithTransactionDateAndUserAndDeliveryAddressAndDeliveryProviderAndPaymentMethodAndOrderedProductListArguments() {

        Order order = new Order(RANDOM_TRANSACTION_DATE, RANDOM_USER, RANDOM_DELIVERY_ADDRESS, RANDOM_DELIVERY_PROVIDER,
                RANDOM_PAYMENT_METHOD, RANDOM_ORDERED_PRODUCT_LIST);

        assertNull(order.getId());
        assertEquals(order.getTransactionDate(), RANDOM_TRANSACTION_DATE);
        assertEquals(order.getUser(), RANDOM_USER);
        assertEquals(order.getDeliveryAddress(), RANDOM_DELIVERY_ADDRESS);
        assertEquals(order.getDeliveryProvider(), RANDOM_DELIVERY_PROVIDER);
        assertEquals(order.getPaymentMethod(), RANDOM_PAYMENT_METHOD);
        assertEquals(order.getOrderedProducts(), RANDOM_ORDERED_PRODUCT_LIST);
    }
}
