package com.example.Restaurant;


import com.example.Restaurant.controller.ProductController;
import com.example.Restaurant.dto.ProductDto;
import com.example.Restaurant.entity.Product;
import com.example.Restaurant.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@Testcontainers
public class ProductControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15").withDatabaseName("restaurant").withUsername("postgres").withPassword("user");

    @DynamicPropertySource
    static void configureProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductController productController;


    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }


    @Test
    public void shouldGetAllProduct() {
        //given
        Product product = new Product(10L, "Coffee", 1000);
        Product product1 = new Product(12L, "Water", 500);
        Product product2 = new Product(13L, "Cola", 4000);
        Product product3 = new Product(14L, "Pepsi", 4000);
        Product product4 = new Product(16L, "White Coffee", 5000);
        Product product5 = new Product(20L, "Latte", 10000);
        productRepository.saveAll(List.of(product, product1, product2, product3, product4, product5));

        //when
        List<ProductDto> productDtoList = productController.displayProductList().getBody();
        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), productController.displayProductList().getStatusCode());
        Assertions.assertEquals(6, productDtoList.size());

        //todo: don't rely on order
        Assertions.assertEquals(product.getProductName(), productDtoList.get(0).productName());
        Assertions.assertEquals(product1.getProductName(), productDtoList.get(1).productName());
        Assertions.assertEquals(product2.getProductName(), productDtoList.get(2).productName());
        Assertions.assertEquals(product3.getProductName(), productDtoList.get(3).productName());
        Assertions.assertEquals(product4.getProductName(), productDtoList.get(4).productName());
        Assertions.assertEquals(product5.getProductName(), productDtoList.get(5).productName());

    }


    @Test
    public void shouldFindProductDetails() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        Product product1 = new Product(2L, "Water", 500);
        Product product2 = new Product(3L, "Fanta", 1500);
        // todo: saveAll
        productRepository.save(product);
        productRepository.save(product1);
        productRepository.save(product2);
        //when
        ProductDto getProductDetails = productController.productDetails("Pepsi").getBody();
        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), productController.productDetails("Pepsi").getStatusCode());
        Assertions.assertEquals("Pepsi", getProductDetails.productName());
        Assertions.assertEquals(1000, getProductDetails.price());
    }


    @Test
    public void shouldCreateProduct() {
        //then
        String productName = "7UP";
        int price = 1000;

        //when
        ProductDto productDto = productController.createProducts(new ProductDto(productName, price)).getBody();

        //given
        Assertions.assertEquals(HttpStatusCode.valueOf(200), productController.createProducts(new ProductDto("Tonic", price)).getStatusCode());
        Assertions.assertEquals(productName, productDto.productName());
        Assertions.assertEquals(price, productDto.price());
    }


    @Test
    public void shouldDeleteProduct() {
        //given
        Product product = new Product(1L, "Pepsi", 1000);
        Product product1 = new Product(2L, "7UP", 1000);
        // todo: saveAll
        productRepository.save(product);
        productRepository.save(product1);

        //when
        productController.deleteProduct("Pepsi");
        List<ProductDto> getProductDtoList = productController.displayProductList().getBody();


        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(204), productController.deleteProduct("7UP").getStatusCode());
        Assertions.assertEquals(1, getProductDtoList.size());
        Assertions.assertEquals("7UP", getProductDtoList.stream().map(ProductDto::productName).findFirst().get());
    }
}
