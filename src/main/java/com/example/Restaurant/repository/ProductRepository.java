package com.example.Restaurant.repository;

import com.example.Restaurant.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductName(String productName);

    Product findByProductName(String productName);

    int deleteByProductName(String productName);
}
