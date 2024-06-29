package com.example.Restaurant;


import com.example.Restaurant.controller.TableController;
import com.example.Restaurant.dto.TableDto;
import com.example.Restaurant.entity.ProductOnTable;
import com.example.Restaurant.entity.ProductsOnTable;
import com.example.Restaurant.entity.Table;
import com.example.Restaurant.entity.TableStatus;
import com.example.Restaurant.repository.TableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TableControllerIntegrationTest extends TestContainer {
    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private TableController tableController;

    @Test
    void shouldDisplayTableList() {
        //given
        Table tableForDelete = new Table(3L, 8, 8, new ProductsOnTable(), TableStatus.FREE, 0);
        tableRepository.delete(tableForDelete);
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.FREE, 0);
        Table table1 = new Table(2L, 4, 0, new ProductsOnTable(), TableStatus.FREE, 0);
        tableRepository.saveAll(List.of(table, table1));
        //when
        List<TableDto> tableList = tableController.tableList().getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.tableList().getStatusCode());
        assertNotNull(tableList);
        assertEquals(2, tableList.size());
    }

    @Test
    public void shouldFindTablesByTableList() {
        //given
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.FREE, 0);
        Table table1 = new Table(2L, 4, 0, new ProductsOnTable(), TableStatus.FREE, 0);
        Table table3 = new Table(4L, 4, 0, new ProductsOnTable(), TableStatus.OCCUPIED_WITH_PRODUCTS, 0);
        tableRepository.saveAll(List.of(table, table1, table3));
        //when
        List<TableDto> freeTableList = tableController.tableStatusList("free").getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.tableStatusList("free").getStatusCode());
        assertEquals(2, freeTableList.size());
        assertEquals(TableStatus.FREE, freeTableList.get(0).tableStatus());
        assertEquals(TableStatus.FREE, freeTableList.get(1).tableStatus());
    }

    @Test
    public void shouldCreateTable() {
        //given
        int seatsInNewTable = 8;
        //when
        TableDto createTable = tableController.createTable(8).getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.createTable(seatsInNewTable).getStatusCode());
        assertEquals(8, createTable.availableSeats());
        assertEquals(8, createTable.seats());
        assertEquals(TableStatus.FREE, createTable.tableStatus());
    }


    @Test
    public void shouldChangeTableStatusToFree() {
        //given
        Table table4 = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.PAID, 0);
        tableRepository.save(table4);
        //when
        TableDto tableDto = tableController.openPaidTable(1L).getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.openPaidTable(table4.getTableId()).getStatusCode());
        assertEquals(TableStatus.FREE, tableDto.tableStatus());
    }

    @Test
    public void shouldChangeTableStatusToPaid() {
        //given
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.PAID, 0);
        tableRepository.save(table);
        //when
        TableDto tableDto = tableController.closePaidTable(1L).getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.openPaidTable(table.getTableId()).getStatusCode());
        assertEquals(TableStatus.PAID, tableDto.tableStatus());
    }


    @Test
    public void ShouldChangeTableStatusToOccupied() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.OCCUPIED, 0);
        tableRepository.save(table);
        //when
        TableDto tableDto = tableController.tableReservation(1L, 4).getBody();
        //given
        assertEquals(HttpStatusCode.valueOf(200), tableController.tableReservation(table.getTableId(), 4).getStatusCode());
        assertEquals(TableStatus.OCCUPIED, tableDto.tableStatus());
        assertEquals(0, tableDto.availableSeats());
    }

    @Test
    public void ShouldChangeTableStatusToFreeByCancelReservation() {
        //given
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.OCCUPIED, 0);
        tableRepository.save(table);
        //when
        TableDto tableDto = tableController.cancelOccupied(1L).getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.cancelOccupied(1L).getStatusCode());
        assertEquals(TableStatus.FREE, tableDto.tableStatus());
    }

    @Test
    public void shouldChangeProductOnTable() {
        //given
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.OCCUPIED, 0);
        ProductOnTable product = new ProductOnTable(1L, 1000, 3, "Pepsi");
        ProductsOnTable productsOnTable = new ProductsOnTable(new HashSet<>(List.of(product)));
        tableRepository.save(table);
        //when
        TableDto tableDto = tableController.changeOrder(1L, productsOnTable.getProducts()).getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.changeOrder(1L, productsOnTable.getProducts()).getStatusCode());
        assertNotNull(tableDto);
        assertEquals(3000, tableDto.valueOfTheBill());
        assertEquals(tableDto.productsOnTable().getProducts(), productsOnTable.getProducts());
    }

    @Test
    public void shouldDeleteProductOnTable() {
        //given
        ProductOnTable product = new ProductOnTable(1L, 1000, 3, "Pepsi");
        ProductOnTable product1 = new ProductOnTable(1L, 1000, 2, "Pepsi");
        ProductsOnTable productsOnTable = new ProductsOnTable(new HashSet<>(List.of(product)));
        ProductsOnTable deleteProductFromTable = new ProductsOnTable(new HashSet<>(List.of(product1)));
        Table table = new Table(1L, 4, 0, productsOnTable, TableStatus.OCCUPIED, 0);
        tableRepository.save(table);
        //when
        TableDto tableDto = tableController.deleteOrderList(1L, deleteProductFromTable.getProducts()).getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), tableController.deleteOrderList(1L, deleteProductFromTable.getProducts()).getStatusCode());
        assertNotNull(tableDto);
        assertEquals(1000, tableDto.valueOfTheBill());
        assertEquals(1, tableDto.productsOnTable().getProducts().stream().mapToInt(ProductOnTable::getQuantity).sum());
    }


}
