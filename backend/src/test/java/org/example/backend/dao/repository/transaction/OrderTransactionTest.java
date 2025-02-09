package org.example.backend.dao.repository.transaction;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.entity.transaction.OrderedProduct;
import org.example.backend.dao.repository.logistic.AddressRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@DataJpaTest
public class OrderTransactionTest {

    private final String RANDOM_DELIVERY_PROVIDER_NAME = "Random delivery provider";
    private final String RANDOM_COUNTRY_NAME = "Random country";
    private final String RANDOM_PROVINCE_NAME = "Random province";
    private final String RANDOM_CITY_NAME = "Random city";
    private final String RANDOM_ADDRESS = "Random address";
    private final String RANDOM_PAYMENT_METHOD = "Random payment method";
    private final Long RANDOM_QUANTITY = 1L;
    private final Double RANDOM_PRICE_PER_UNIT = 10.00;
    private final String RANDOM_EAN_CODE = "32211421412";
    private final String RANDOM_PRODUCT_NAME = "Random product name";
    private final String RANDOM_TYPE = "Random type";
    private final String RANDOM_PRODUCT_DESCRIPTION = "Random product description";
    private final Double RANDOM_PRICE = 5.00;
    private final byte[] RANDOM_MAIN_IMAGE = new byte[10];
    private final byte[] DIFFERENT_IMAGE = new byte[12];
    private final Date TODAYS_DATE = Date.from(ZonedDateTime.now().toInstant());

    @Autowired
    private OrderTransactionRepository orderTransactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void testOfSave(){

        Stock stock = new Stock(RANDOM_QUANTITY);
        ProductMainImage productMainImage = new ProductMainImage(RANDOM_MAIN_IMAGE);
        ProductPageImage productPageImage = new ProductPageImage(DIFFERENT_IMAGE);
        Product product = new Product(RANDOM_PRODUCT_NAME, RANDOM_EAN_CODE, RANDOM_TYPE, RANDOM_PRODUCT_DESCRIPTION,
                RANDOM_PRICE, RANDOM_PRICE, stock, productMainImage, List.of(productPageImage));
        productRepository.save(product);

        Address address = new Address(RANDOM_COUNTRY_NAME, RANDOM_PROVINCE_NAME, RANDOM_CITY_NAME, RANDOM_ADDRESS);
        addressRepository.save(address);

        OrderedProduct orderedProduct = new OrderedProduct(product, RANDOM_QUANTITY, RANDOM_PRICE);

        //TODO Finish this test
    }
}
