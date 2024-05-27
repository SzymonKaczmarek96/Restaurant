package com.example.Restaurant.entity;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public enum TableStatus {
    OCCUPIED,FREE,OCCUPIED_WITH_PRODUCTS,PAID
}
