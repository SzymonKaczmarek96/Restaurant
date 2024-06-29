package com.example.Restaurant.service;

import com.example.Restaurant.dto.ProductDto;
import com.example.Restaurant.entity.Product;
import com.example.Restaurant.exceptions.ProductAlreadyExistsException;
import com.example.Restaurant.exceptions.ProductNotExistsException;
import com.example.Restaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getProductList() {
        return productRepository.findAll().stream().map(Product::toProductDto).toList();
    }

    public ProductDto getProductDetails(String productName) {
        return productRepository.findByProductName(productName)
                .orElseThrow(() -> new ProductNotExistsException(productName)).toProductDto();
    }

    public ProductDto createProductIfNotExists(ProductDto productDto) {
        productRepository.findByProductName(productDto.productName())
                .ifPresent(product -> {
                    throw new ProductAlreadyExistsException(productDto.productName());
                });
        Product enityProduct = new Product(productDto.productName());
        enityProduct.setPrice(productDto.price());
        return productRepository.save(enityProduct).toProductDto();
    }

    @Transactional
    public ProductDto updateProductByName(String productName, ProductDto productDto) {
        Product productForUpdate = productRepository.findByProductName(productName)
                .orElseThrow(() -> new ProductNotExistsException(productName));
        if (productForUpdate.getProductName().equals(productDto.productName())) {
            throw new ProductAlreadyExistsException(productForUpdate.getProductName());
        }
        productForUpdate.setProductName(productDto.productName());
        productForUpdate.setPrice(productDto.price());
        return productRepository.save(productForUpdate).toProductDto();
    }

    @Transactional
    public void deleteProductDetails(String productName) {
        int deleteRecord = productRepository.deleteByProductName(productName);
        if (deleteRecord == 0) {
            throw new ProductNotExistsException(productName);
        }
    }


}
