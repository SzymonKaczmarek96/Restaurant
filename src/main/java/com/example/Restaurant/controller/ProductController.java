package com.example.Restaurant.controller;


import com.example.Restaurant.dto.ProductDto;
import com.example.Restaurant.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> displayProductList() {
        List<ProductDto> productDtoList = productService.getProductList();
        return ResponseEntity.ok(productDtoList);
    }

    @GetMapping("/{productName}")
    public ResponseEntity<ProductDto> productDetails(@PathVariable String productName) {
        ProductDto productDtoDetails = productService.getProductDetails(productName);
        return ResponseEntity.ok(productDtoDetails);
    }

    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProducts(@RequestBody ProductDto productDto) {
        ProductDto createTheNewProduct = productService.createProductIfNotExists(productDto);
        return ResponseEntity.ok().body(createTheNewProduct);
    }

    @PutMapping("/{productName}/update")
    public ResponseEntity<ProductDto> updateProductDetails(@PathVariable String productName, @RequestBody ProductDto productDto) {
        ProductDto updatedProductDetails = productService.updateProductByName(productName, productDto);
        return ResponseEntity.ok().body(updatedProductDetails);
    }

    @DeleteMapping("/{productName}/delete")
    public ResponseEntity deleteProduct(@PathVariable String productName) {
        productService.deleteProductDetails(productName);
        return ResponseEntity.noContent().build();
    }


}
