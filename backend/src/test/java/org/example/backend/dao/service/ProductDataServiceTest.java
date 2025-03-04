package org.example.backend.dao.service;

import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.repository.image.ProductMainImageRepository;
import org.example.backend.dao.repository.image.ProductPageImageRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.image.ProductPageImageNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.product.ProductNotSavedException;
import org.example.backend.model.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductDataServiceTest {

    private final UUID ID_OF_PRODUCT_WHICH_EXIST = UUID.randomUUID();
    private final UUID ID_OF_PRODUCT_WHICH_NOT_EXIST = UUID.randomUUID();
    private final UUID ID_OF_PAGE_IMAGE_THAT_EXIST = UUID.randomUUID();
    private final UUID ID_OF_PAGE_IMAGE_THAT_NOT_EXIST = UUID.randomUUID();
    private final String RANDOM_PRODUCT_NAME = "Random product name";
    private final String WRONG_EAN_CODE = "DHADABDB232";
    private final String OCCUPIED_EAN_CODE = "18921008";
    private final String DIFFERENT_8_SIGN_EAN_CODE = "71021038";
    private final String DIFFERENT_13_SIGN_EAN_CODE = "7102103839021";
    private final String RANDOM_TYPE = "random";
    private final String TYPE_THAT_NOT_EXIST = "notexist";
    private final String RANDOM_DESCRIPTION = "random description";
    private final Integer NEGATIVE_HEIGHT = -50;
    private final Integer RANDOM_HEIGHT = 100;
    private final Integer NEGATIVE_WIDTH = -50;
    private final Integer RANDOM_WIDTH = 100;
    private final Double NEGATIVE_PRICE = -10.00;
    private final Long RANDOM_STOCK = 1000L;
    private final Long DIFFERENT_STOCK = 76L;
    private final Long GREATER_STOCK = 100000L;
    private final Long NEGATIVE_STOCK = -10L;
    private final byte[] RANDOM_IMAGE = new byte[12];
    private final String RANDOM_PHRASE = "Random product";
    private final Double RANDOM_PRICE = 100.00;
    private final Double RANDOM_CURRENT_PRICE = 80.00;
    private final Double LOWER_PRICE_THAN_CURRENT_PRICE = 50.00;
    private final Double GREATER_PRICE_THAN_CURRENT_PRICE_1 = 110.00;
    private final Double GREATER_PRICE_THAN_CURRENT_PRICE_2 = 150.00;

    @Mock
    ProductMainImageRepository productMainImageRepository;

    @Mock
    ProductPageImageRepository productPageImageRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    private ProductDataService productDataService;

    private Product product;
    private List<Product> list_of_products;

    @BeforeEach
    public void setUp() {
        product = new Product(RANDOM_PRODUCT_NAME, OCCUPIED_EAN_CODE, RANDOM_TYPE, RANDOM_DESCRIPTION, RANDOM_HEIGHT,
                RANDOM_WIDTH, RANDOM_PRICE, RANDOM_PRICE, new Stock(RANDOM_STOCK), new ProductMainImage(RANDOM_IMAGE));
        product.getPageImages().add(new ProductPageImage(ID_OF_PAGE_IMAGE_THAT_EXIST, RANDOM_IMAGE, product));
        product.setId(ID_OF_PRODUCT_WHICH_EXIST);
        list_of_products = new ArrayList<>();
        list_of_products.add(product);
    }

    @Test
    public void testOfSaveNewProduct() {

        when(productRepository.findByEANCode(OCCUPIED_EAN_CODE)).thenReturn(product);
        when(productRepository.findByEANCode(DIFFERENT_8_SIGN_EAN_CODE)).thenReturn(null);
        when(productRepository.findByEANCode(DIFFERENT_13_SIGN_EAN_CODE)).thenReturn(null);
        when(productRepository.save(any())).thenReturn(product);

        ProductModel productModel = ProductModel
                .builder()
                .name(RANDOM_PRODUCT_NAME)
                .EANCode(DIFFERENT_13_SIGN_EAN_CODE)
                .type(RANDOM_TYPE)
                .description(RANDOM_DESCRIPTION)
                .height(RANDOM_HEIGHT)
                .width(RANDOM_WIDTH)
                .regularPrice(RANDOM_PRICE)
                .currentPrice(RANDOM_PRICE)
                .mainImage(RANDOM_IMAGE)
                .build();

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.saveNewProduct(null, RANDOM_STOCK);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productModel.setEANCode(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productModel.setEANCode(WRONG_EAN_CODE);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setEANCode(DIFFERENT_8_SIGN_EAN_CODE);

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setName(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setName("");
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setName(RANDOM_PRODUCT_NAME);

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setType(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            productModel.setType("");
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setType(RANDOM_TYPE);

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setDescription(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setDescription("");
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setDescription(RANDOM_DESCRIPTION);

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setHeight(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception eleventhException = assertThrows(BadArgumentException.class, () -> {
            productModel.setHeight(NEGATIVE_HEIGHT);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setHeight(RANDOM_HEIGHT);

        Exception twelfthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setWidth(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception thirteenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setWidth(NEGATIVE_WIDTH);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setWidth(RANDOM_WIDTH);

        Exception fourteenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setRegularPrice(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception fifteenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setRegularPrice(NEGATIVE_PRICE);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setRegularPrice(RANDOM_PRICE);

        Exception sixteenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setCurrentPrice(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        Exception seventeenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setCurrentPrice(NEGATIVE_PRICE);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setCurrentPrice(RANDOM_PRICE);

        Exception eighteenthException = assertThrows(BadArgumentException.class, () -> {
            productModel.setMainImage(null);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        productModel.setMainImage(RANDOM_IMAGE);

        Exception nineteenthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.saveNewProduct(productModel, null);
        });

        Exception twentiethException = assertThrows(BadArgumentException.class, () -> {
            productDataService.saveNewProduct(productModel, NEGATIVE_STOCK);
        });

        Exception twentyfirstException = assertThrows(ProductNotSavedException.class, () -> {
            productModel.setEANCode(OCCUPIED_EAN_CODE);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        assertDoesNotThrow(() -> {
            productModel.setEANCode(DIFFERENT_8_SIGN_EAN_CODE);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        assertDoesNotThrow(() -> {
            productModel.setEANCode(DIFFERENT_13_SIGN_EAN_CODE);
            productDataService.saveNewProduct(productModel, RANDOM_STOCK);
        });

        assertEquals(firstException.getMessage(), "Null argument: productModel");
        assertEquals(secondException.getMessage(), "Incorrect argument field: productModel.EANCode");
        assertEquals(thirdException.getMessage(), "Incorrect argument field: productModel.EANCode");
        assertEquals(fourthException.getMessage(), "Incorrect argument field: productModel.name");
        assertEquals(fifthException.getMessage(), "Incorrect argument field: productModel.name");
        assertEquals(sixthException.getMessage(), "Incorrect argument field: productModel.type");
        assertEquals(seventhException.getMessage(), "Incorrect argument field: productModel.type");
        assertEquals(eighthException.getMessage(), "Incorrect argument field: productModel.description");
        assertEquals(ninthException.getMessage(), "Incorrect argument field: productModel.description");
        assertEquals(tenthException.getMessage(), "Incorrect argument field: productModel.height");
        assertEquals(eleventhException.getMessage(), "Incorrect argument field: productModel.height");
        assertEquals(twelfthException.getMessage(), "Incorrect argument field: productModel.width");
        assertEquals(thirteenthException.getMessage(), "Incorrect argument field: productModel.width");
        assertEquals(fourteenthException.getMessage(), "Incorrect argument field: productModel.regularPrice");
        assertEquals(fifteenthException.getMessage(), "Incorrect argument field: productModel.regularPrice");
        assertEquals(sixteenthException.getMessage(), "Incorrect argument field: productModel.currentPrice");
        assertEquals(seventeenthException.getMessage(), "Incorrect argument field: productModel.currentPrice");
        assertEquals(eighteenthException.getMessage(), "Incorrect argument field: productModel.mainImage");
        assertEquals(nineteenthException.getMessage(), "Incorrect argument: stock");
        assertEquals(twentiethException.getMessage(), "Incorrect argument: stock");
        assertEquals(twentyfirstException.getMessage(), "Product with ean code " + OCCUPIED_EAN_CODE + " already exists");
    }

    @Test
    public void testOfUpdateProductDescriptionById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductDescriptionById(null, RANDOM_DESCRIPTION);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductDescriptionById(ID_OF_PRODUCT_WHICH_EXIST, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductDescriptionById(ID_OF_PRODUCT_WHICH_EXIST, "");
        });

        Exception fourthException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.updateProductDescriptionById(ID_OF_PRODUCT_WHICH_NOT_EXIST, RANDOM_DESCRIPTION);
        });

        assertDoesNotThrow(() -> {
            productDataService.updateProductDescriptionById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_DESCRIPTION);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: description");
        assertEquals(thirdException.getMessage(), "Incorrect argument: description");
        assertEquals(fourthException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfAddProductStockQuantityById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.addProductQuantityById(null, DIFFERENT_STOCK);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.addProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.addProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, NEGATIVE_STOCK);
        });

        Exception fourthException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.addProductQuantityById(ID_OF_PRODUCT_WHICH_NOT_EXIST, DIFFERENT_STOCK);
        });

        assertDoesNotThrow(() -> {
            productDataService.addProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, DIFFERENT_STOCK);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: stock");
        assertEquals(thirdException.getMessage(), "Incorrect argument: stock");
        assertEquals(fourthException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfReduceProductStockQuantityById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.reduceProductQuantityById(null, DIFFERENT_STOCK);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.reduceProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, null);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.reduceProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, NEGATIVE_STOCK);
        });

        Exception fourthException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.reduceProductQuantityById(ID_OF_PRODUCT_WHICH_NOT_EXIST, DIFFERENT_STOCK);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.reduceProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, GREATER_STOCK);
        });

        assertDoesNotThrow(() -> {
            productDataService.reduceProductQuantityById(ID_OF_PRODUCT_WHICH_EXIST, DIFFERENT_STOCK);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: stock");
        assertEquals(thirdException.getMessage(), "Incorrect argument: stock");
        assertEquals(fourthException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
        assertEquals(fifthException.getMessage(), "Incorrect argument: stock");
    }

    @Test
    public void testOfUpdateProductSizeById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductSizeById(null, RANDOM_HEIGHT, RANDOM_WIDTH);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductSizeById(ID_OF_PRODUCT_WHICH_EXIST, null, RANDOM_WIDTH);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductSizeById(ID_OF_PRODUCT_WHICH_EXIST, NEGATIVE_HEIGHT, RANDOM_WIDTH);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductSizeById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_HEIGHT, null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductSizeById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_HEIGHT, NEGATIVE_WIDTH);
        });

        Exception sixthException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.updateProductSizeById(ID_OF_PRODUCT_WHICH_NOT_EXIST, RANDOM_HEIGHT, RANDOM_WIDTH);
        });

        assertDoesNotThrow(() -> {
            productDataService.updateProductSizeById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_HEIGHT, RANDOM_WIDTH);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: height");
        assertEquals(thirdException.getMessage(), "Incorrect argument: height");
        assertEquals(fourthException.getMessage(), "Incorrect argument: width");
        assertEquals(fifthException.getMessage(), "Incorrect argument: width");
        assertEquals(sixthException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfUpdateProductRegularPriceAndCurrentPriceById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(null, GREATER_PRICE_THAN_CURRENT_PRICE_1, RANDOM_PRICE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_EXIST, null,
                    RANDOM_PRICE);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_EXIST, NEGATIVE_PRICE,
                    RANDOM_PRICE);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_PRICE,
                    null);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_PRICE,
                    NEGATIVE_PRICE);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1);
        });

        Exception seventhException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_NOT_EXIST, RANDOM_PRICE,
                    RANDOM_PRICE);
        });

        assertDoesNotThrow(() -> {
            productDataService.updateProductRegularPriceAndCurrentPriceById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_PRICE,
                    RANDOM_PRICE);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Incorrect argument: regularPrice");
        assertEquals(thirdException.getMessage(), "Incorrect argument: regularPrice");
        assertEquals(fourthException.getMessage(), "Incorrect argument: currentPrice");
        assertEquals(fifthException.getMessage(), "Incorrect argument: currentPrice");
        assertEquals(sixthException.getMessage(), "Current price mustn't be greater than regular price");
        assertEquals(seventhException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfUpdateProductMainImageById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductMainImageById(null, RANDOM_IMAGE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.updateProductMainImageById(ID_OF_PRODUCT_WHICH_EXIST, null);
        });

        Exception thirdException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.updateProductMainImageById(ID_OF_PRODUCT_WHICH_NOT_EXIST, RANDOM_IMAGE);
        });

        assertDoesNotThrow(() -> {
            productDataService.updateProductMainImageById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_IMAGE);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Null argument: newMainImage");
        assertEquals(thirdException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfAddProductPageImageById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.addProductPageImageById(null, RANDOM_IMAGE);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.addProductPageImageById(ID_OF_PRODUCT_WHICH_EXIST, null);
        });

        Exception thirdException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.addProductPageImageById(ID_OF_PRODUCT_WHICH_NOT_EXIST, RANDOM_IMAGE);
        });

        assertDoesNotThrow(() -> {
            productDataService.addProductPageImageById(ID_OF_PRODUCT_WHICH_EXIST, RANDOM_IMAGE);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Null argument: newPageImage");
        assertEquals(thirdException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfDeleteProductPageImageById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.deleteProductPageImageById(null, ID_OF_PAGE_IMAGE_THAT_EXIST);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.deleteProductPageImageById(ID_OF_PRODUCT_WHICH_EXIST, null);
        });

        Exception thirdException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.deleteProductPageImageById(ID_OF_PRODUCT_WHICH_NOT_EXIST, ID_OF_PAGE_IMAGE_THAT_EXIST);
        });

        Exception fourthException = assertThrows(ProductPageImageNotFoundException.class, () -> {
            productDataService.deleteProductPageImageById(ID_OF_PRODUCT_WHICH_EXIST, ID_OF_PAGE_IMAGE_THAT_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            productDataService.deleteProductPageImageById(ID_OF_PRODUCT_WHICH_EXIST, ID_OF_PAGE_IMAGE_THAT_EXIST);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Null argument: pageImageId");
        assertEquals(thirdException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
        assertEquals(fourthException.getMessage(), "Product page image with id " + ID_OF_PAGE_IMAGE_THAT_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetProductById(){

        when(productRepository.findById(ID_OF_PRODUCT_WHICH_EXIST)).thenReturn(Optional.of(product));
        when(productRepository.findById(ID_OF_PRODUCT_WHICH_NOT_EXIST)).thenReturn(Optional.empty());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductById(null);
        });

        Exception secondException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.getProductById(ID_OF_PRODUCT_WHICH_NOT_EXIST);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductById(ID_OF_PRODUCT_WHICH_EXIST);
        });

        assertEquals(firstException.getMessage(), "Null argument: id");
        assertEquals(secondException.getMessage(), "Product with id " + ID_OF_PRODUCT_WHICH_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetProductsByPhrase(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhrase(null, List.of());
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhrase("", List.of());
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByPhrase(RANDOM_PHRASE, List.of());
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: phrase");
        assertEquals(secondException.getMessage(), "Incorrect argument: phrase");
    }

    @Test
    public void testOfGetProductsByType(){

        List<String> list = new ArrayList<>();

        when(productRepository.findByType(eq(RANDOM_TYPE), eq(list), any())).thenReturn(list_of_products);
        when(productRepository.findByType(eq(TYPE_THAT_NOT_EXIST), eq(list), any())).thenReturn(List.of());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByType(null, list);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByType("", list);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByType(RANDOM_TYPE, null);
        });

        Exception fourthException = assertThrows(ProductNotFoundException.class, () -> {
            productDataService.getProductsByType(TYPE_THAT_NOT_EXIST, list);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByType(RANDOM_TYPE, list);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: type");
        assertEquals(secondException.getMessage(), "Incorrect argument: type");
        assertEquals(thirdException.getMessage(), "Null argument: forbiddenEanCodes");
        assertEquals(fourthException.getMessage(), "Products with type " + TYPE_THAT_NOT_EXIST + " not found");
    }

    @Test
    public void testOfGetProductsByPriceRange(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPriceRange(null, RANDOM_PRICE, List.of());
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPriceRange(NEGATIVE_PRICE, RANDOM_PRICE, List.of());
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPriceRange(RANDOM_PRICE, null, List.of());
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPriceRange(RANDOM_PRICE, NEGATIVE_PRICE, List.of());
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService
                    .getProductsByPriceRange(GREATER_PRICE_THAN_CURRENT_PRICE_1, RANDOM_PRICE, List.of());
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            productDataService
                    .getProductsByPriceRange(LOWER_PRICE_THAN_CURRENT_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_2, null);
        });

        assertDoesNotThrow(() -> {
            productDataService
                    .getProductsByPriceRange(LOWER_PRICE_THAN_CURRENT_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_2, List.of());
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(secondException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(thirdException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(fourthException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(fifthException.getMessage(), "Argument minimalPrice mustn't be greater than maximalPrice");
        assertEquals(sixthException.getMessage(), "Null argument: forbiddenEanCodes");
    }

    @Test
    public void testOfGetProductsByTypeAndPhrase(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhrase(null, RANDOM_PHRASE, List.of());
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhrase("", RANDOM_PHRASE, List.of());
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhrase(RANDOM_TYPE, null, List.of());
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhrase(RANDOM_TYPE, "", List.of());
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhrase(RANDOM_TYPE, RANDOM_PHRASE, null);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByTypeAndPhrase(RANDOM_TYPE, RANDOM_PHRASE, List.of());
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: type");
        assertEquals(secondException.getMessage(), "Incorrect argument: type");
        assertEquals(thirdException.getMessage(), "Incorrect argument: phrase");
        assertEquals(fourthException.getMessage(), "Incorrect argument: phrase");
        assertEquals(fifthException.getMessage(), "Null argument: forbiddenEanCodes");
    }

    @Test
    public void testOfGetProductsByTypeAndPriceRange(){

        List<String> list = new ArrayList<>();

        when(productRepository.findByTypeAndPriceRange(eq(RANDOM_TYPE), eq(LOWER_PRICE_THAN_CURRENT_PRICE),
                eq(GREATER_PRICE_THAN_CURRENT_PRICE_1), eq(list), any())).thenReturn(new ArrayList<>());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(null, LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange("", LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, null,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, NEGATIVE_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    null, list);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    NEGATIVE_PRICE, list);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, GREATER_PRICE_THAN_CURRENT_PRICE_1,
                    LOWER_PRICE_THAN_CURRENT_PRICE, list);
        });

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_2, null);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByTypeAndPriceRange(RANDOM_TYPE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: type");
        assertEquals(secondException.getMessage(), "Incorrect argument: type");
        assertEquals(thirdException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(fourthException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(fifthException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(sixthException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(seventhException.getMessage(), "Argument minimalPrice mustn't be greater than maximalPrice");
        assertEquals(eighthException.getMessage(), "Null argument: forbiddenEanCodes");
    }

    @Test
    public void testOfGetProductsByPhraseAndPriceRange(){

        List<String> list = new ArrayList<>();

        when(productRepository.findByPhraseAndPriceRange(eq(RANDOM_PHRASE), eq(LOWER_PRICE_THAN_CURRENT_PRICE),
                eq(GREATER_PRICE_THAN_CURRENT_PRICE_1), eq(list), any())).thenReturn(new ArrayList<>());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhraseAndPriceRange(null, LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhraseAndPriceRange("", LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhraseAndPriceRange(RANDOM_PHRASE, null,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhraseAndPriceRange(RANDOM_PHRASE, NEGATIVE_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhraseAndPriceRange(RANDOM_PHRASE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    null, list);
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByPhraseAndPriceRange(RANDOM_PHRASE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    NEGATIVE_PRICE, list);
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            productDataService
                    .getProductsByPhraseAndPriceRange(RANDOM_PHRASE, GREATER_PRICE_THAN_CURRENT_PRICE_1,
                    LOWER_PRICE_THAN_CURRENT_PRICE, list);
        });

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            productDataService
                    .getProductsByPhraseAndPriceRange(RANDOM_PHRASE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, null);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByPhraseAndPriceRange(RANDOM_PHRASE, LOWER_PRICE_THAN_CURRENT_PRICE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, list);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: phrase");
        assertEquals(secondException.getMessage(), "Incorrect argument: phrase");
        assertEquals(thirdException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(fourthException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(fifthException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(sixthException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(seventhException.getMessage(), "Argument minimalPrice mustn't be greater than maximalPrice");
        assertEquals(ninthException.getMessage(), "Null argument: forbiddenEanCodes");
    }

    @Test
    public void testOfGetProductsByTypeAndPhraseAndPriceRanges(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(null, RANDOM_PHRASE,
                    RANDOM_PRICE, RANDOM_PRICE, List.of());
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange("", RANDOM_PHRASE,
                    RANDOM_PRICE, RANDOM_PRICE, List.of());
        });

        Exception thirdException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, null,
                    RANDOM_PRICE, RANDOM_PRICE, List.of());
        });

        Exception fourthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, "",
                    RANDOM_PRICE, RANDOM_PRICE, List.of());
        });

        Exception fifthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    null, RANDOM_PRICE, List.of());
        });

        Exception sixthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    NEGATIVE_PRICE, RANDOM_PRICE, List.of());
        });

        Exception seventhException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    RANDOM_PRICE, null, List.of());
        });

        Exception eighthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    RANDOM_PRICE, NEGATIVE_PRICE, List.of());
        });

        Exception ninthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    GREATER_PRICE_THAN_CURRENT_PRICE_1, RANDOM_PRICE, List.of());
        });

        Exception tenthException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    LOWER_PRICE_THAN_CURRENT_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_1, null);
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsByTypeAndPhraseAndPriceRange(RANDOM_TYPE, RANDOM_PHRASE,
                    RANDOM_PRICE, GREATER_PRICE_THAN_CURRENT_PRICE_1, List.of());
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: type");
        assertEquals(secondException.getMessage(), "Incorrect argument: type");
        assertEquals(thirdException.getMessage(), "Incorrect argument: phrase");
        assertEquals(fourthException.getMessage(), "Incorrect argument: phrase");
        assertEquals(fifthException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(sixthException.getMessage(), "Incorrect argument: minimalPrice");
        assertEquals(seventhException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(eighthException.getMessage(), "Incorrect argument: maximalPrice");
        assertEquals(ninthException.getMessage(), "Argument minimalPrice mustn't be greater than maximalPrice");
        assertEquals(tenthException.getMessage(), "Null argument: forbiddenEanCodes");
    }

    @Test
    public void testOfGetProductsAndRelatedToThemQuantityByPhrase(){

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsAndRelatedToThemQuantityByPhrase(null);
        });

        Exception secondException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getProductsAndRelatedToThemQuantityByPhrase("");
        });

        assertDoesNotThrow(() -> {
            productDataService.getProductsAndRelatedToThemQuantityByPhrase(RANDOM_PHRASE);
        });

        assertEquals(firstException.getMessage(), "Incorrect argument: phrase");
        assertEquals(secondException.getMessage(), "Incorrect argument: phrase");
    }

    @Test
    public void testOfGetAllProductImagesByProductId(){

        when(productPageImageRepository.findByProductId(ID_OF_PRODUCT_WHICH_EXIST))
                .thenReturn(new ArrayList<>());

        Exception firstException = assertThrows(BadArgumentException.class, () -> {
            productDataService.getAllProductImagesByProductId(null);
        });

        assertDoesNotThrow(() -> {
            productDataService.getAllProductImagesByProductId(ID_OF_PRODUCT_WHICH_EXIST);
        });

        assertEquals(firstException.getMessage(), "Null argument: productId");
    }

    @Test
    public void testOfGetAllProductTypes(){

        when(productRepository.getAllProductTypes())
                .thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> {
            productDataService.getAllProductTypes();
        });
    }

    @Test
    public void testOfDeleteProductById(){

        Exception exception = assertThrows(BadArgumentException.class, () -> {
            productDataService.deleteProductById(null);
        });

        assertDoesNotThrow(() -> {
            productDataService.deleteProductById(ID_OF_PRODUCT_WHICH_EXIST);
        });

        assertEquals(exception.getMessage(), "Null argument: id");
    }
}
