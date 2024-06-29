package com.example.Restaurant;

import com.example.Restaurant.dto.ProductDto;
import com.example.Restaurant.entity.Product;
import com.example.Restaurant.exceptions.ProductAlreadyExistsException;
import com.example.Restaurant.exceptions.ProductNotExistsException;
import com.example.Restaurant.repository.ProductRepository;
import com.example.Restaurant.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void shouldDisplayProductList() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        Product product1 = new Product(2L, "Fanta", 1000);
        Product product2 = new Product(3L, "Cola", 1000);
        Product product3 = new Product(4L, "7up", 1000);
        List<Product> products = Arrays.asList(product, product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);
        //when
        List<ProductDto> result = productService.getProductList();
        //then
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void shouldDisplayProductDetails() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        when(productRepository.findByProductName(product.getProductName())).thenReturn(Optional.of(product));
        //when
        ProductDto productDetails = productService.getProductDetails(product.getProductName());
        //then
        assertNotNull(productDetails);
        assertEquals("Pepsi", productDetails.productName());
        assertEquals(1000, productDetails.price());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        //when
        assertThrows(ProductNotExistsException.class, () -> productService.getProductDetails("Sprite"));
    }

    @Test
    void shouldCreateTheNewProduct() {
        //given
        Product product = new Product(2L, "Pepsi", 1000);
        ProductDto productDto = new ProductDto("Coffee", 1000);
        when(productRepository.save(any(Product.class))).thenReturn(new Product(1L, productDto.productName(), productDto.price()));
        //when
        ProductDto productIfNotExists = productService.createProductIfNotExists(productDto);
        //then
        assertEquals("Coffee", productIfNotExists.productName());
        assertEquals(1000, productIfNotExists.price());
    }

    @Test
    void shouldThrowExceptionWhenProductExists() {
        //given
        Product existingProduct = new Product(1L, "Pepsi", 1000);
        ProductDto productDto = new ProductDto("Pepsi", 1000);
        //when
        when(productRepository.findByProductName("Pepsi")).thenReturn(Optional.of(existingProduct));
        //then
        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProductIfNotExists(productDto));
    }

    @Test
    void shouldChangeProductDetails() {
        //given
        Product product = new Product(2L, "Pepsi", 1000);
        ProductDto productDto = new ProductDto("Water", 500);
        when(productRepository.findByProductName(product.getProductName())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(new Product(1L, productDto.productName(), productDto.price()));
        //when
        ProductDto updatedProduct = productService.updateProductByName(product.getProductName(), productDto);
        //then
        assertEquals("Water", updatedProduct.productName());
        assertEquals(500, updatedProduct.price());
        verify(productRepository, times(1)).findByProductName("Pepsi");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotExist() {
        //given
        ProductDto productDto = new ProductDto("Water", 500);
        //when
        assertThrows(ProductNotExistsException.class, () -> productService.updateProductByName("Pepsi", productDto));
    }


    @Test
    void shouldThrowExceptionWhenProductHaveTheSameName() {
        //given
        Product product = new Product(2L, "Water", 1000);
        ProductDto productDto = new ProductDto("Water", 500);
        //when
        when(productRepository.findByProductName(product.getProductName())).thenReturn(Optional.of(product));
        //then
        assertThrows(ProductAlreadyExistsException.class, () -> productService.updateProductByName(product.getProductName(), productDto));
    }

    @Test
    void shouldThrowExceptionWhenProductPriceIsNegative() {
        //given
        Product product = new Product(2L, "Water", 1000);
        ProductDto productDto = new ProductDto("Pepsi", -1);
        //when
        when(productRepository.findByProductName(product.getProductName())).thenReturn(Optional.of(product));
        //then
        assertThrows(IllegalArgumentException.class, () -> productService.updateProductByName(product.getProductName(), productDto));
    }

    @Test
    void shouldDeleteProduct() {
        //given
        Product product = new Product(2L, "Water", 1000);
        when(productRepository.deleteByProductName("Water")).thenReturn(1);
        //when
        productService.deleteProductDetails("Water");
        //then
        verify(productRepository, times(1)).deleteByProductName(product.getProductName());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        //given
        String productName = "Water";
        //when
        assertThrows(ProductNotExistsException.class, () -> productService.deleteProductDetails(productName));
    }


}
