package com.example.Restaurant.service;

import com.example.Restaurant.dto.ProductDto;
import com.example.Restaurant.entity.Product;
import com.example.Restaurant.exceptions.ProductAlreadyExistsException;
import com.example.Restaurant.exceptions.ProductNotExistsException;
import com.example.Restaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getProductList(){
        List<ProductDto> allRecords = productRepository.findAll().stream().map(Product::toProductDto).toList();
        return allRecords;
    }

    public ProductDto getProductDetails(String productName){
        if(productRepository.existsByProductName(productName)){
        Product productDetails = productRepository.findByProductName(productName);
        return productDetails.toProductDto();
        }
        throw new ProductNotExistsException(productName);
    }

    public ProductDto createProductIfNotExists(ProductDto productDto){
        if(productRepository.existsByProductName(productDto.productName())){
            throw new ProductAlreadyExistsException(productDto.productName());
        }
        Product enityProduct = new Product(productDto.productName());
        enityProduct.setPrice(productDto.price());

        return productRepository.save(enityProduct).toProductDto();
    }
}
