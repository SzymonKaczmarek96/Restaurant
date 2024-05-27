package com.example.Restaurant;

import com.example.Restaurant.dto.ProductDto;
import com.example.Restaurant.entity.Product;
import com.example.Restaurant.exceptions.ProductNotExistsException;
import com.example.Restaurant.repository.ProductRepository;
import com.example.Restaurant.service.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
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

//	@BeforeEach
//	void setup(){
//		MockitoAnnotations.openMocks(this);
//	}

	@Test
	void shouldDisplayProductList() {
		//given
		Product product = new Product(1L,"Pepsi",1000);
		Product product1 = new Product(1L,"Fanta",1000);
		Product product2 = new Product(1L,"Cola",1000);
		Product product3 = new Product(1L,"7up",1000);
		List<Product> products;
		products = Arrays.asList(product,product1,product2,product3);
		when(productRepository.findAll()).thenReturn(products);
		//when
		List<ProductDto> result = productService.getProductList();


		//then
		assertNotNull(result);
		assertEquals("Pepsi",result.get(0).productName());
		assertEquals("Fanta",result.get(1).productName());
		assertEquals("Cola",result.get(2).productName());
		assertEquals("7up",result.get(3).productName());


	}

	@Test
	void shouldDisplayProductDetails(){
		//given
		Product product = new Product(1L,"Pepsi",1000);
		when(productRepository.existsByProductName(product.getProductName())).thenReturn(true);
		when(productRepository.findByProductName(product.getProductName())).thenReturn(product);
		//then
		ProductDto productDetails = productService.getProductDetails(product.getProductName());
		//when
		assertNotNull(productDetails);
		assertEquals("Pepsi",productDetails.productName());
		assertEquals(1000,productDetails.price());
		verify(productRepository,times(1)).existsByProductName(productDetails.productName());
		verify(productRepository,times(1)).findByProductName(productDetails.productName());
	}

	@Test
	void shouldThrowException(){
		Product product = new Product(1L,"Pepsi",1000);
		when(productRepository.existsByProductName("Sprite")).thenReturn(false);
		lenient().when(productRepository.findByProductName("Sprite")).thenReturn(null);
		assertThrows(ProductNotExistsException.class , () -> productService.getProductDetails("Sprite"));
	}

	@Test
	void shouldCreateTheNewProduct(){
		Product product = new Product(2L,"Pepsi",1000);
		ProductDto productDto = new ProductDto("Coffee", 1000);

		lenient().when(productRepository.existsByProductName(product.getProductName())).thenReturn(false);
		lenient().when(productRepository.findByProductName(product.getProductName())).thenReturn(null);
		when(productRepository.save(any(Product.class)))
				.thenReturn(new Product(1L, productDto.productName(), productDto.price()));

		ProductDto productIfNotExists = productService.createProductIfNotExists(productDto);

		assertEquals("Coffee",productIfNotExists.productName());
		assertEquals(1000,productIfNotExists.price());

		verify(productRepository, times(1)).existsByProductName(productDto.productName());
		verify(productRepository, times(1)).save(any(Product.class));
	}


}
