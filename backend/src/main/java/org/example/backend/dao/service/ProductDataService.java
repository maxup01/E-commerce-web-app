package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.repository.image.ProductMainImageRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.product.ProductNotSavedException;
import org.example.backend.model.user.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

//TODO change return value types to data models
@Service
public class ProductDataService {

    Pattern ean8Pattern;
    Pattern ean13Pattern;
    
    private ProductMainImageRepository productMainImageRepository;

    private ProductRepository productRepository;

    @Autowired
    public ProductDataService(ProductMainImageRepository productMainImageRepository, ProductRepository productRepository) {
        this.productMainImageRepository = productMainImageRepository;
        this.productRepository = productRepository;
        this.ean8Pattern = Pattern.compile("^[0-9]{8}$");
        this.ean13Pattern = Pattern.compile("^[0-9]{13}$");
    }

    @Transactional
    public Product saveNewProduct(ProductModel productModel, Long stock){

        if(productModel == null)
            throw new BadArgumentException("Null argument: productModel");
        else if((productModel.getEANCode() == null) ||
                (!((ean8Pattern.matcher(productModel.getEANCode()).matches()) || (ean13Pattern.matcher(productModel.getEANCode()).matches()))))
            throw new BadArgumentException("Incorrect argument field: productModel.EANCode");
        else if((productModel.getName() == null) || (productModel.getName().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: productModel.name");
        else if((productModel.getType() == null) || (productModel.getType().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: productModel.type");
        else if((productModel.getDescription() == null) || (productModel.getDescription().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: productModel.description");
        else if((productModel.getHeight() == null) || (productModel.getHeight() <= 0))
            throw new BadArgumentException("Incorrect argument field: productModel.height");
        else if((productModel.getWidth() == null) || (productModel.getWidth() <= 0))
            throw new BadArgumentException("Incorrect argument field: productModel.width");
        else if((productModel.getRegularPrice() == null) || (productModel.getRegularPrice() <= 0))
            throw new BadArgumentException("Incorrect argument field: productModel.regularPrice");
        else if((productModel.getCurrentPrice() == null) || (productModel.getCurrentPrice() <= 0))
            throw new BadArgumentException("Incorrect argument field: productModel.currentPrice");
        else if(productModel.getMainImage() == null)
            throw new BadArgumentException("Incorrect argument field: productModel.mainImage");
        else if((stock == null) || (stock <= 0))
            throw new BadArgumentException("Incorrect argument: stock");

        Product foundProduct = productRepository.findByEANCode(productModel.getEANCode());

        if(foundProduct != null)
            throw new ProductNotSavedException("Product with ean code " + productModel.getEANCode() + " already exists");

        Stock stockEntity = new Stock(stock);
        
        ProductMainImage productMainImage = new ProductMainImage(productModel.getMainImage());

        Product product = new Product(productModel.getName(), productModel.getEANCode(), productModel.getType(),
                productModel.getDescription(), productModel.getHeight(), productModel.getWidth(), productModel.getRegularPrice(),
                productModel.getCurrentPrice(), stockEntity, productMainImage);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProductDescriptionById(UUID id, String description){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((description == null) || (description.isEmpty()))
            throw new BadArgumentException("Incorrect argument: description");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.setDescription(description);
        return productRepository.save(foundProduct);
    }

    @Transactional
    public Product updateProductStockQuantityById(UUID id, Long stock){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((stock == null) || (stock < 0))
            throw new BadArgumentException("Incorrect argument: stock");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.getStock().setQuantity(stock);
        return productRepository.save(foundProduct);
    }

    @Transactional
    public Product updateProductSizeById(UUID id, Integer height, Integer width){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((height == null) || (height <= 0))
            throw new BadArgumentException("Incorrect argument: height");
        else if((width == null) || (width <= 0))
            throw new BadArgumentException("Incorrect argument: width");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.setHeight(height);
        foundProduct.setWidth(width);
        return productRepository.save(foundProduct);
    }

    @Transactional
    public Product getProductById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        return productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });
    }

    @Transactional
    public List<Product> getProductsByType(String type){

        if(type == null)
            throw new BadArgumentException("Null argument: type");

        List<Product> foundProducts = productRepository.findByType(type);

        if(foundProducts.isEmpty())
            throw new ProductNotFoundException("Products with type " + type + " not found");

        return foundProducts;
    }
}
