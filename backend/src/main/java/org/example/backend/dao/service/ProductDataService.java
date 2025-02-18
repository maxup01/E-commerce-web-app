package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.image.ProductMainImage;
import org.example.backend.dao.entity.image.ProductPageImage;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.entity.product.Stock;
import org.example.backend.dao.repository.image.ProductMainImageRepository;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.image.ProductPageImageNotFoundException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.product.ProductNotSavedException;
import org.example.backend.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

//TODO change return value types to data models in all methods
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
        else if((productModel.getName() == null) || (productModel.getName().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: productModel.name");
        else if((productModel.getType() == null) || (productModel.getType().trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument field: productModel.type");
        else if((productModel.getDescription() == null) || (productModel.getDescription().trim().isEmpty()))
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
    public ProductModel updateProductDescriptionById(UUID id, String description){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((description == null) || (description.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: description");

        Product product = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        product.setDescription(description);
        productRepository.save(product);

        return new ProductModel(product.getId(), product.getEANCode(), product.getName(),
                product.getType(), product.getDescription(), product.getHeight(), product.getWidth(), product.getRegularPrice(),
                product.getCurrentPrice(), product.getMainImage().getImage());
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
    public Product updateProductRegularPriceAndCurrentPriceById(UUID id, Double regularPrice, Double currentPrice){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((regularPrice == null) || (regularPrice <= 0))
            throw new BadArgumentException("Incorrect argument: regularPrice");
        else if((currentPrice == null) || (currentPrice <= 0))
            throw new BadArgumentException("Incorrect argument: currentPrice");
        else if(currentPrice > regularPrice)
            throw new BadArgumentException("Current price mustn't be greater than regular price");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.setRegularPrice(regularPrice);
        foundProduct.setCurrentPrice(currentPrice);
        return productRepository.save(foundProduct);
    }

    @Transactional
    public Product updateProductMainImageById(UUID id, byte[] newMainImage){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if(newMainImage == null)
            throw new BadArgumentException("Null argument: newMainImage");

        Product product = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        ProductMainImage productMainImage = new ProductMainImage(newMainImage);

        if(product.getMainImage() == null){
            product.setMainImage(productMainImage);
        }
        else {
            productMainImageRepository.delete(product.getMainImage());
            product.setMainImage(productMainImage);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product addProductPageImageById(UUID id, byte[] newPageImage){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if(newPageImage == null)
            throw new BadArgumentException("Null argument: newPageImage");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.getPageImages().add(new ProductPageImage(newPageImage));
        return productRepository.save(foundProduct);
    }

    @Transactional
    public Product deleteProductPageImageById(UUID id, UUID pageImageId){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if(pageImageId == null)
            throw new BadArgumentException("Null argument: pageImageId");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        int arraySize = foundProduct.getPageImages().size();

        foundProduct.getPageImages().removeIf(productPageImage -> productPageImage.getId().equals(pageImageId));

        if(arraySize == foundProduct.getPageImages().size())
            throw new ProductPageImageNotFoundException("Product page image with id " + pageImageId + " not found");

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
    public List<Product> getProductsByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        return productRepository.findByPhrase(phrase);
    }

    @Transactional
    public List<Product> getProductsByType(String type){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Product> foundProducts = productRepository.findByType(type);

        if(foundProducts.isEmpty())
            throw new ProductNotFoundException("Products with type " + type + " not found");

        return foundProducts;
    }

    @Transactional
    public List<Product> getProductsByTypeAndPhrase(String type, String phrase){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");
        else if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        return productRepository.findByPhraseAndType(phrase, type);
    }

    @Transactional
    public List<Product> getProductsByPriceRange(Double minimalPrice, Double maximalPrice){

        if((minimalPrice == null) || (minimalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: minimalPrice");
        else if((maximalPrice == null) || (maximalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: maximalPrice");
        else if(minimalPrice > maximalPrice)
            throw new BadArgumentException("Argument minimalPrice mustn't be greater than maximalPrice");

        return productRepository.findByPriceRange(minimalPrice, maximalPrice);
    }

    @Transactional
    public List<Product> getProductsByTypeAndPhraseAndPriceRange(String type, String phrase, Double minimalPrice,
                                                                  Double maximalPrice){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");
        else if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");
        else if((minimalPrice == null) || (minimalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: minimalPrice");
        else if((maximalPrice == null) || (maximalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: maximalPrice");
        else if(minimalPrice > maximalPrice)
            throw new BadArgumentException("Argument minimalPrice mustn't be greater than maximalPrice");

        return productRepository.findByPhraseAndTypeAndPriceRanges(phrase, type, minimalPrice, maximalPrice);
    }

    @Transactional
    public List<Product> getProductsOnSale(){
        return productRepository.showOnSale();
    }

    @Transactional
    public Long getTotalQuantityOfAllProducts(){
        return productRepository.getTotalQuantityOfProducts();
    }

    @Transactional
    public List<Object[]> getAllTypesOfProductsAndRelatedToThemQuantity(){
        return productRepository.getTypesAndQuantityOfProductsWithThisTypes();
    }

    @Transactional
    public List<Object[]> getProductsAndRelatedToThemQuantityByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        return productRepository.getProductsAndRelatedQuantityByPhrase(phrase);
    }
}
