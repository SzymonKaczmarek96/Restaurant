package com.example.Restaurant;


import com.example.Restaurant.entity.ProductOnTable;
import com.example.Restaurant.entity.ProductsOnTable;
import com.example.Restaurant.entity.Table;
import com.example.Restaurant.entity.TableStatus;
import com.example.Restaurant.exceptions.InvalidTableStatusTransitionException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TableTest {
    @Test
    void shouldChangeTableStatus() {
        //given
        Table table = new Table(TableStatus.FREE);
        //when
        table.setTableStatus(TableStatus.OCCUPIED);
        //then
        assertEquals(TableStatus.OCCUPIED, table.getTableStatus());
    }

    @Test
    void shouldThrowInvalidTableStatusTransitionExceptionIfStatusNotExists() {
        //given
        Table table = new Table();
        //when
        assertThrows(InvalidTableStatusTransitionException.class, () -> table.setTableStatus(TableStatus.FREE));
    }

    @Test
    void shouldAddProductsToTableAndUpdateStatus() {
        //given
        ProductOnTable product = new ProductOnTable(2L, 2000, 3, "Coffee");
        ProductOnTable product1 = new ProductOnTable(3L, 3000, 2, "Latte");
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.OCCUPIED);
        ProductsOnTable productsOnTable = new ProductsOnTable(new HashSet<>(Arrays.asList(product, product1)));
        //when
        table.setProductsOnTable(productsOnTable);
        //then
        assertEquals(2, table.getProductsOnTable().getProducts().size());
        assertEquals(TableStatus.OCCUPIED_WITH_PRODUCTS, table.getTableStatus());
        assertEquals(12000, table.getValueOfTheBill());
    }
    
    @Test
    void shouldClearProductsAndUpdateTableStatusToPaidWhenProductsAreRemoved() {
        //then
        ProductOnTable product = new ProductOnTable(2L, 2000, 3, "Coffee");
        ProductOnTable product1 = new ProductOnTable(3L, 3000, 2, "Latte");
        ProductsOnTable productsOnTable = new ProductsOnTable(new HashSet<>(Arrays.asList(product, product1)));
        Table table = new Table(1L, 4, 0, productsOnTable, TableStatus.OCCUPIED);
        //when
        table.setProductsOnTable(new ProductsOnTable());
        //then
        assertEquals(0, table.getProductsOnTable().getProducts().size());
        assertEquals(TableStatus.PAID, table.getTableStatus());
        assertEquals(0, table.getValueOfTheBill());
    }


}
