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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.setDescription(description);
        productRepository.save(foundProduct);

        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public ProductModel updateProductStockQuantityById(UUID id, Long stock){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if((stock == null) || (stock < 0))
            throw new BadArgumentException("Incorrect argument: stock");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.getStock().setQuantity(stock);
        productRepository.save(foundProduct);

        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public ProductModel updateProductSizeById(UUID id, Integer height, Integer width){

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
        productRepository.save(foundProduct);


        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public ProductModel updateProductRegularPriceAndCurrentPriceById(UUID id, Double regularPrice, Double currentPrice){

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
        productRepository.save(foundProduct);

        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public ProductModel updateProductMainImageById(UUID id, byte[] newMainImage){

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

        productRepository.save(product);

        return ProductModel.fromProduct(product);
    }

    @Transactional
    public ProductModel addProductPageImageById(UUID id, byte[] newPageImage){

        if(id == null)
            throw new BadArgumentException("Null argument: id");
        else if(newPageImage == null)
            throw new BadArgumentException("Null argument: newPageImage");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        foundProduct.getPageImages().add(new ProductPageImage(newPageImage));
        productRepository.save(foundProduct);

        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public ProductModel deleteProductPageImageById(UUID id, UUID pageImageId){

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

        productRepository.save(foundProduct);

        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public ProductModel getProductById(UUID id){

        if(id == null)
            throw new BadArgumentException("Null argument: id");

        Product foundProduct = productRepository.findById(id).orElseThrow(() -> {
            return new ProductNotFoundException("Product with id " + id + " not found");
        });

        return ProductModel.fromProduct(foundProduct);
    }

    @Transactional
    public List<ProductModel> getProductsByType(String type){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");

        List<Product> foundProducts = productRepository.findByType(type);

        if(foundProducts.isEmpty())
            throw new ProductNotFoundException("Products with type " + type + " not found");

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsByPhrase(String phrase){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Product> products = productRepository.findByPhrase(phrase);

        ArrayList<ProductModel> productModels = new ArrayList<>();

        products.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsByPriceRange(Double minimalPrice, Double maximalPrice){

        if((minimalPrice == null) || (minimalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: minimalPrice");
        else if((maximalPrice == null) || (maximalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: maximalPrice");
        else if(minimalPrice > maximalPrice)
            throw new BadArgumentException("Argument minimalPrice mustn't be greater than maximalPrice");

        List<Product> foundProducts = productRepository.findByPriceRange(minimalPrice, maximalPrice);

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsByTypeAndPhrase(String type, String phrase){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");
        else if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");

        List<Product> foundProducts = productRepository.findByPhraseAndType(phrase, type);

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsByTypeAndPriceRange(String type, Double minimalPrice, Double maximalPrice){

        if((type == null) || (type.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: type");
        else if((minimalPrice == null) || (minimalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: minimalPrice");
        else if((maximalPrice == null) || (maximalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: maximalPrice");
        else if(minimalPrice > maximalPrice)
            throw new BadArgumentException("Argument minimalPrice mustn't be greater than maximalPrice");

        List<Product> foundProducts = productRepository
                .findByTypeAndPriceRange(type, minimalPrice, maximalPrice);

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsByPhraseAndPriceRange(String phrase, Double minimalPrice, Double maximalPrice){

        if((phrase == null) || (phrase.trim().isEmpty()))
            throw new BadArgumentException("Incorrect argument: phrase");
        else if((minimalPrice == null) || (minimalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: minimalPrice");
        else if((maximalPrice == null) || (maximalPrice <= 0))
            throw new BadArgumentException("Incorrect argument: maximalPrice");
        else if(minimalPrice > maximalPrice)
            throw new BadArgumentException("Argument minimalPrice mustn't be greater than maximalPrice");

        List<Product> foundProducts = productRepository
                .findByPhraseAndPriceRange(phrase, minimalPrice, maximalPrice);

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsByTypeAndPhraseAndPriceRange(String type, String phrase, Double minimalPrice,
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

        List<Product> foundProducts = productRepository
                .findByPhraseAndTypeAndPriceRange(phrase, type, minimalPrice, maximalPrice);

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
    }

    @Transactional
    public List<ProductModel> getProductsOnSale(){

        List<Product> foundProducts = productRepository.showOnSale();

        ArrayList<ProductModel> productModels = new ArrayList<>();

        foundProducts.forEach(product -> {
            productModels.add(ProductModel.fromProduct(product));
        });

        return productModels;
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

        ArrayList<Object[]> rowsWithProductModel = new ArrayList<>();

        List<Object[]> resultList = productRepository.getProductsAndRelatedQuantityByPhrase(phrase);

        resultList.forEach(row -> {

            Object[] newRow = new Object[2];
            newRow[0] = ProductModel.fromProduct((Product) row[0]);
            newRow[1] = row[1];

            rowsWithProductModel.add(newRow);
        });

        return rowsWithProductModel;
    }
}
