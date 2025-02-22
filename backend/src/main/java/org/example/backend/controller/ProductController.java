package org.example.backend.controller;

import org.example.backend.dao.service.ProductDataService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.product.ProductNotSavedException;
import org.example.backend.model.ProductModel;
import org.example.backend.model.ProductModelAndStock;
import org.example.backend.model.ProductSearchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
