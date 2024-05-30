package com.example.Restaurant.entity;


import com.example.Restaurant.dto.ProductDto;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;


    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;


    @Column(name = "price", nullable = false)
    private int price;


    public ProductDto toProductDto() {
        ProductDto productDto = new ProductDto(productName, price);
        return productDto;
    }

    public Product(String productName) {
        this.productName = productName;
    }

    public void setPrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price can't be less than 0");
        }
        this.price = price;
    }

}
