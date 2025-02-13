package org.example.backend.dao.service;

import jakarta.transaction.Transactional;
import org.example.backend.dao.entity.product.Product;
import org.example.backend.dao.repository.product.ProductRepository;
import org.example.backend.exception.global.BadArgumentException;
import org.example.backend.exception.product.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductDataService {

    @Autowired
    private ProductRepository productRepository;

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
