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
        Product product1 = new Product(1L, "Fanta", 1000);
        Product product2 = new Product(1L, "Cola", 1000);
        Product product3 = new Product(1L, "7up", 1000);
        List<Product> products;
        products = Arrays.asList(product, product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);
        //when
        List<ProductDto> result = productService.getProductList();
        //then
        assertNotNull(result);
        assertEquals("Pepsi", result.get(0).productName());
        assertEquals("Fanta", result.get(1).productName());
        assertEquals("Cola", result.get(2).productName());
        assertEquals("7up", result.get(3).productName());
    }

    @Test
    void shouldDisplayProductDetails() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
        when(productRepository.findByProductName(product.getProductName())).thenReturn(product);
        //when
        ProductDto productDetails = productService.getProductDetails(product.getProductName());
        //then

        assertNotNull(productDetails);
        assertEquals("Pepsi", productDetails.productName());
        assertEquals(1000, productDetails.price());
        verify(productRepository, times(1)).existsByProductName(productDetails.productName());
        verify(productRepository, times(1)).findByProductName(productDetails.productName());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        //when
        when(productRepository.existsByProductName("Sprite")).thenReturn(false);
        lenient().when(productRepository.findByProductName("Sprite")).thenReturn(null);

        //then
        assertThrows(ProductNotExistsException.class, () -> productService.getProductDetails("Sprite"));
    }

    @Test
    void shouldCreateTheNewProduct() {
        //given
        Product product = new Product(2L, "Pepsi", 1000);
        ProductDto productDto = new ProductDto("Coffee", 1000);
        lenient().when(productRepository.existsByProductName(product.getProductName())).thenReturn(false);
        lenient().when(productRepository.findByProductName(product.getProductName())).thenReturn(null);
        when(productRepository.save(any(Product.class)))
                .thenReturn(new Product(1L, productDto.productName(), productDto.price()));
        //when
        ProductDto productIfNotExists = productService.createProductIfNotExists(productDto);
        //then
        assertEquals("Coffee", productIfNotExists.productName());
        assertEquals(1000, productIfNotExists.price());
        verify(productRepository, times(1)).existsByProductName(productDto.productName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductExists() {
        Product product = new Product(2L, "Pepsi", 1000);
        ProductDto productDto = new ProductDto("Pepsi", 1000);
        when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProductIfNotExists(productDto));
    }

    @Test
    void shouldChangeProductDetails() {
        //given
        Product product = new Product(2L, "Pepsi", 1000);
        ProductDto productDto = new ProductDto("Water", 500);
        lenient().when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
        lenient().when(productRepository.findByProductName(product.getProductName())).thenReturn(product);
        when(productRepository.save(any(Product.class)))
                .thenReturn(new Product(1L, productDto.productName(), productDto.price()));
        //when
        ProductDto updatedProduct = productService.updateProductByName(product.getProductName(), productDto);
        //then
        assertEquals("Water", updatedProduct.productName());
        assertEquals(500, updatedProduct.price());
        verify(productRepository, times(1)).findByProductName("Pepsi");
        verify(productRepository, times(1)).existsByProductName("Pepsi");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotExist() {
        //given
        ProductDto productDto = new ProductDto("Water", 500);
        //when
        when(productRepository.existsByProductName("Pepsi")).thenReturn(false);
        //then
        assertThrows(ProductNotExistsException.class, () -> productService.updateProductByName("Pepsi", productDto));
    }


    @Test
    void shouldThrowExceptionWhenProductHaveTheSameName() {
        //given
        Product product = new Product(2L, "Water", 1000);
        ProductDto productDto = new ProductDto("Water", 500);
        //when
        lenient().when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
        lenient().when(productRepository.findByProductName(product.getProductName())).thenReturn(product);
        //then
        assertThrows(ProductAlreadyExistsException.class, () ->
                productService.updateProductByName(product.getProductName(), productDto));
    }

    @Test
    void shouldThrowExceptionWhenProductPriceIsNegative() {
        //given
        Product product = new Product(2L, "Water", 1000);
        ProductDto productDto = new ProductDto("Pepsi", -1);
        //when
        lenient().when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
        lenient().when(productRepository.findByProductName(product.getProductName())).thenReturn(product);
        //then
        assertThrows(IllegalArgumentException.class, () ->
                productService.updateProductByName(product.getProductName(), productDto));
    }

    @Test
    void shouldDeleteProduct() {
        //given
        Product product = new Product(2L, "Water", 1000);
        //when
        lenient().when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
        productService.deleteProductDetails(product.getProductName());
        //then
        verify(productRepository, times(1)).deleteByProductName(product.getProductName());
    }

    @Test
    void shouldNotDeleteProductWhenNotExist() {
        //given
        String productName = "Water";

        //when
        when(productRepository.existsByProductName(productName)).thenReturn(false);

        //then
        assertThrows(ProductNotExistsException.class, () -> productService.deleteProductDetails(productName));
    }


}
