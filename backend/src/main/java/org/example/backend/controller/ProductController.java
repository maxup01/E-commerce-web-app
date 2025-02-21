package org.example.backend.controller;

import org.example.backend.dao.service.ProductDataService;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.model.ProductModel;
import org.example.backend.model.ProductSearchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    private final ProductDataService productDataService;

    @Autowired
    public ProductController(ProductDataService productDataService) {
        this.productDataService = productDataService;
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
}
