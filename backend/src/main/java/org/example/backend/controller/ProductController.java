package org.example.backend.controller;

import org.example.backend.dao.service.ProductDataService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.example.backend.exception.product.ProductNotSavedException;
import org.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class ProductController {

    private final ProductDataService productDataService;

    @Autowired
    public ProductController(ProductDataService productDataService) {
        this.productDataService = productDataService;
    }

    @PostMapping("/products/create")
    public ResponseEntity<ProductModelAndStock> createProduct(@RequestBody ProductModelAndStock productModelAndStock) {

        if(productModelAndStock == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ProductModelAndStock response;

        try{
            response = productDataService.saveNewProduct(productModelAndStock.getProduct(),
                    productModelAndStock.getStock());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotSavedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/products/update")
    public ResponseEntity<ProductModel> updateProduct(@RequestBody ProductModel productModel) {

        if(productModel == null)
            throw new BadArgumentException("Null argument: productModel");

        ProductModel existingProduct = productDataService.getProductById(productModel.getId());

        if(existingProduct == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if(!Objects.equals(productModel.getDescription(), existingProduct.getDescription())){

            try{
                existingProduct = productDataService
                        .updateProductDescriptionById(existingProduct.getId(), productModel.getDescription());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } catch (ProductNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        if((!Objects.equals(productModel.getHeight(), existingProduct.getHeight())) ||
                (!Objects.equals(productModel.getWidth(), existingProduct.getWidth()))){

            try{
                existingProduct = productDataService
                        .updateProductSizeById(existingProduct.getId(), productModel.getHeight(), productModel.getWidth());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } catch (ProductNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        if((!Objects.equals(productModel.getRegularPrice(), existingProduct.getRegularPrice())) ||
                (!Objects.equals(productModel.getCurrentPrice(), existingProduct.getCurrentPrice()))){

            try{
                existingProduct = productDataService
                        .updateProductRegularPriceAndCurrentPriceById(
                                existingProduct.getId(), productModel.getRegularPrice(), productModel.getCurrentPrice());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } catch (ProductNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        if(productModel.getMainImage() != existingProduct.getMainImage()){

            try{
                existingProduct = productDataService
                        .updateProductMainImageById(existingProduct.getId(), productModel.getMainImage());
            } catch (BadArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } catch (ProductNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(existingProduct);
    }

    @PutMapping("/products/add-quantity")
    public ResponseEntity<ProductModelAndStock> addProductQuantity(
            @RequestBody ProductModelAndStock productModelAndStock) {

        if(productModelAndStock == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        ProductModelAndStock result;

        try{
            result = productDataService
                    .addProductQuantityById(productModelAndStock.getProduct().getId(), productModelAndStock.getStock());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/products/reduce-quantity")
    public ResponseEntity<ProductModelAndStock> reduceProductQuantity(
            @RequestBody ProductModelAndStock productModelAndStock) {

        if(productModelAndStock == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        ProductModelAndStock result;

        try{
            result = productDataService
                    .reduceProductQuantityById(productModelAndStock.getProduct().getId(), productModelAndStock.getStock());
        } catch (BadArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/products/insert-page-images")
    public ResponseEntity<ProductModelAndPageImages> insertProductPageImage(
            @RequestBody ProductIdAndPageImage model) {

        if(model == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        ProductModelAndPageImages result;

        try{
            result = productDataService.addProductPageImageById(model.getProductId(), model.getImage());
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/products/delete-page-images")
    public ResponseEntity<ProductModelAndPageImages> deleteProductPageImage(
            @RequestBody ProductIdAndPageImageIdModel model) {

        if(model == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        ProductModelAndPageImages result;

        try{
            result = productDataService.deleteProductPageImageById(model.getProductId(), model.getPageImageId());
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getProductsByProductSearchModel(
            @RequestBody ProductSearchModel productSearchModel) {

        List<ProductModel> productModels;

        if((productSearchModel.getType() != null) && (productSearchModel.getPhrase() != null) &&
        (productSearchModel.getMinPrice() != null) && (productSearchModel.getMaxPrice() != null)) {

            try{
                productModels = productDataService
                        .getProductsByTypeAndPhraseAndPriceRange(productSearchModel.getType(),
                                productSearchModel.getPhrase(), productSearchModel.getMinPrice(),
                                productSearchModel.getMaxPrice());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((productSearchModel.getType() != null) && (productSearchModel.getPhrase() != null)) {

            try{
                productModels = productDataService
                        .getProductsByTypeAndPhrase(productSearchModel.getType(), productSearchModel.getPhrase());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((productSearchModel.getType() != null) && (productSearchModel.getMinPrice() != null) &&
        (productSearchModel.getMaxPrice() != null)) {

            try{
                productModels = productDataService
                        .getProductsByTypeAndPriceRange(productSearchModel.getType(), productSearchModel.getMinPrice(),
                                productSearchModel.getMaxPrice());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((productSearchModel.getPhrase() != null) && (productSearchModel.getMinPrice() != null) &&
        (productSearchModel.getMaxPrice() != null)) {

            try{
                productModels = productDataService
                        .getProductsByPhraseAndPriceRange(productSearchModel.getPhrase(), productSearchModel.getMinPrice(),
                                productSearchModel.getMaxPrice());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(productSearchModel.getType() != null) {

            try{
                productModels = productDataService
                        .getProductsByType(productSearchModel.getType());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if(productSearchModel.getPhrase() != null){

            try{
                productModels = productDataService
                        .getProductsByPhrase(productSearchModel.getPhrase());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else if((productSearchModel.getMinPrice() != null) && (productSearchModel.getMaxPrice() != null)) {

            try{
                productModels = productDataService
                        .getProductsByPriceRange(productSearchModel.getMinPrice(), productSearchModel.getMaxPrice());
            } catch (BadArgumentException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(productModels);
    }

    @GetMapping("/products/sale")
    public ResponseEntity<List<ProductModel>> getProductsOnSale(){

        return ResponseEntity.ok(productDataService.getProductsOnSale());
    }

    @GetMapping("/admin/products/{phrase}")
    public ResponseEntity<List<Object[]>> getProductsAndRelatedToThemQuantityByPhrase(@PathVariable("phrase") String phrase){

        List<Object[]> result;

        try{
            result = productDataService.getProductsAndRelatedToThemQuantityByPhrase(phrase);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/admin/products/quantity")
    public ResponseEntity<Long> getQuantityOfAllProducts(){

        return ResponseEntity.status(HttpStatus.OK).body(productDataService.getTotalQuantityOfAllProducts());
    }

    @GetMapping("/admin/products/types-and-related-quantity")
    public ResponseEntity<List<Object[]>> getProductTypesAndRelatedToThemQuantity(){

        return ResponseEntity
                .status(HttpStatus.OK).body(productDataService.getAllTypesOfProductsAndRelatedToThemQuantity());
    }

    @DeleteMapping("/admin/product/delete")
    public ResponseEntity deleteProductById(@RequestBody UUID id){

        try{
            productDataService.deleteProductById(id);
        } catch (BadArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
