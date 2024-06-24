package com.example.Restaurant.dto;

import com.example.Restaurant.entity.ProductOnTable;
import com.example.Restaurant.entity.ProductsOnTable;
import com.example.Restaurant.entity.TableStatus;

import java.util.Set;

public record TableDto(int seats, int availableSeats, ProductsOnTable productsOnTable, TableStatus tableStatus, int valueOfTheBill) {
}
