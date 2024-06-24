package com.example.Restaurant.dto;

import com.example.Restaurant.entity.ProductsOnTable;
import com.example.Restaurant.entity.TableStatus;

public record TableDto(int seats, int availableSeats, ProductsOnTable productsOnTable, TableStatus tableStatus,
                       int valueOfTheBill) {
}
