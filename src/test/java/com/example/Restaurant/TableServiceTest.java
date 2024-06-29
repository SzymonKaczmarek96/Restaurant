package com.example.Restaurant;


import com.example.Restaurant.dto.TableDto;
import com.example.Restaurant.entity.ProductOnTable;
import com.example.Restaurant.entity.ProductsOnTable;
import com.example.Restaurant.entity.Table;
import com.example.Restaurant.entity.TableStatus;
import com.example.Restaurant.exceptions.TableNotExistsException;
import com.example.Restaurant.repository.TableRepository;
import com.example.Restaurant.service.TableService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TableServiceTest {

    @Mock
    TableRepository tableRepository;

    @InjectMocks
    TableService tableService;

    @Test
    public void shouldGetAllTables() {
        //given
        ProductsOnTable productsOnTable = new ProductsOnTable();
        Table table = new Table(1L, 4, 4, productsOnTable, TableStatus.FREE, 0);
        Table table1 = new Table(2L, 4, 4, productsOnTable, TableStatus.OCCUPIED, 0);
        Table table2 = new Table(3L, 4, 4, productsOnTable, TableStatus.PAID, 0);
        List<Table> tables = Arrays.asList(table, table1, table2);
        when(tableRepository.findAll()).thenReturn(tables);
        //when
        List<TableDto> tableDtoList = tableService.getAllTables();
        //then
        assertNotNull(tableDtoList);
        assertEquals(3, tableDtoList.size());
        assertEquals(TableStatus.FREE, tableDtoList.stream().filter(tableDto -> table.getTableStatus().equals(TableStatus.FREE)).findFirst().get().tableStatus());
    }

    @Test
    public void shouldFindAllTableByTableStatus() {
        //given
        ProductsOnTable productsOnTable = new ProductsOnTable();
        Table table2 = new Table(1L, 4, 4, productsOnTable, TableStatus.FREE, 0);
        Table table1 = new Table(1L, 4, 4, productsOnTable, TableStatus.FREE, 0);
        Table table3 = new Table(1L, 4, 0, productsOnTable, TableStatus.OCCUPIED, 0);
        when(tableRepository.findByTableStatus(TableStatus.FREE)).thenReturn(Arrays.asList(table1, table2));
        //when
        List<TableDto> freeStatusList = tableService.getAllTablesByStatus("FREE");
        //then
        assertNotNull(freeStatusList);
        assertEquals(2, freeStatusList.size());
    }

    @Test
    public void shouldCreateANewTable() {
        //given

        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.FREE, 0);
        TableDto tableDto = new TableDto(4, 4, new ProductsOnTable(), TableStatus.FREE, 0);
        when(tableRepository.save(any(Table.class))).thenReturn(new Table(table.getTableId(), tableDto.seats(), tableDto.availableSeats(), tableDto.productsOnTable(), tableDto.tableStatus(), tableDto.valueOfTheBill()));
        //when
        TableDto cratedTable = tableService.createTable(tableDto.seats());
        //then
        assertEquals(TableStatus.FREE, cratedTable.tableStatus());
        assertEquals(4, cratedTable.seats());
        assertEquals(4, cratedTable.availableSeats());
        assertEquals(0, cratedTable.valueOfTheBill());
    }

    @Test
    public void shouldThrowIllegalArgumentWhenSeatsAreNegativeValue() {
        //given
        Table table = new Table(1L, -1, 4, new ProductsOnTable(), TableStatus.FREE, 0);
        //when
        assertThrows(IllegalArgumentException.class, () -> tableService.createTable(table.getSeats()));
    }

    @Test
    public void shouldChangeTableStatusToFree() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.PAID, 0);
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //when
        TableDto tableDto = tableService.openTable(1L);
        //then
        assertNotNull(tableDto);
        assertEquals(4, tableDto.seats());
        assertEquals(4, tableDto.availableSeats());
        assertEquals(TableStatus.FREE, tableDto.tableStatus());
        assertEquals(0, tableDto.valueOfTheBill());
    }

    @Test
    public void shouldThrowExceptionWhenOpeningTableForNonExistingTable() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.FREE, 0);
        //then
        assertThrows(TableNotExistsException.class, () -> tableService.openTable(table.getTableId()));
    }


    @Test
    public void shouldChangeTableStatusToPaid() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.OCCUPIED_WITH_PRODUCTS, 0);
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //when
        TableDto tableDto = tableService.closeTable(1L);
        //then
        assertNotNull(tableDto);
        assertEquals(4, tableDto.seats());
        assertEquals(4, tableDto.availableSeats());
        assertEquals(TableStatus.PAID, tableDto.tableStatus());
        assertEquals(0, tableDto.valueOfTheBill());

    }

    @Test
    public void shouldThrowExceptionWhenClosingReservationForNonExistingTable() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.PAID, 0);
        //when
        assertThrows(TableNotExistsException.class, () -> tableService.closeTable(table.getTableId()));
    }

    @Test
    public void shouldChangeTableStatusToFreeWhenCancelReservation() {
        //given
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.OCCUPIED, 0);
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //when
        TableDto tableDto = tableService.cancelReservation(1L);
        //then
        assertNotNull(tableDto);
        assertEquals(4, tableDto.seats());
        assertEquals(4, tableDto.availableSeats());
        assertEquals(TableStatus.FREE, tableDto.tableStatus());
        assertEquals(0, tableDto.valueOfTheBill());
    }

    @Test
    public void shouldThrowExceptionWhenCancellingReservationForNonExistingTable() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.PAID, 0);
        //when
        assertThrows(TableNotExistsException.class, () -> tableService.cancelReservation(2L));
    }


    @Test
    public void shouldChangeTableStatusToOccupiedWhenReserveTable() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.FREE, 0);
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //when
        TableDto tableDto = tableService.reservationTable(1L, 4);
        //given
        assertNotNull(tableDto);
        assertEquals(4, tableDto.seats());
        assertEquals(0, tableDto.availableSeats());
        assertEquals(TableStatus.OCCUPIED, tableDto.tableStatus());
        assertEquals(0, tableDto.valueOfTheBill());
    }

    @Test
    public void shouldThrowIllegalArgumentWhenToManyPeople() {
        //given
        Table table = new Table(1L, 4, 4, new ProductsOnTable(), TableStatus.FREE, 0);
        //when
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //then
        assertThrows(IllegalArgumentException.class, () -> tableService.reservationTable(1L, 5));
    }


    @Test
    public void shouldAddProductOnTable() {
        //given
        Table table = new Table(1L, 4, 0, new ProductsOnTable(), TableStatus.OCCUPIED, 0);
        ProductOnTable product = new ProductOnTable(1L, 1000, 3, "Pepsi");
        ProductsOnTable productsOnTable = new ProductsOnTable(new HashSet<>(List.of(product)));
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //then
        TableDto tableDto = tableService.addProductsToTable(1L, productsOnTable.getProducts());
        //when
        assertNotNull(tableDto);
        assertEquals(4, tableDto.seats());
        assertEquals(0, tableDto.availableSeats());
        assertEquals(TableStatus.OCCUPIED_WITH_PRODUCTS, tableDto.tableStatus());
        assertEquals(3000, tableDto.valueOfTheBill());
        assertEquals(3, tableDto.productsOnTable().getProducts().stream().mapToInt(ProductOnTable::getQuantity).findFirst().getAsInt());
        assertEquals(productsOnTable.getProducts(), tableDto.productsOnTable().getProducts());
    }

    @Test
    void shouldThrowExceptionWhenAddingProductToNonExistingTable() {
        //given
        ProductOnTable product = new ProductOnTable(1L, 1000, 3, "Pepsi");
        ProductsOnTable productsAddToTable = new ProductsOnTable(new HashSet<>(List.of(product)));
        //when
        assertThrows(TableNotExistsException.class, () -> tableService.addProductsToTable(2L, productsAddToTable.getProducts()));
    }


    @Test
    public void shouldDeleteProductOnTable() {
        //given
        ProductOnTable product = new ProductOnTable(1L, 1000, 3, "Pepsi");
        ProductOnTable product1 = new ProductOnTable(1L, 1000, 2, "Pepsi");
        ProductsOnTable productsAddToTable = new ProductsOnTable(new HashSet<>(List.of(product)));
        ProductsOnTable productDeleteFromTable = new ProductsOnTable(new HashSet<>(List.of(product1)));
        Table table = new Table(1L, 4, 0, productsAddToTable, TableStatus.OCCUPIED_WITH_PRODUCTS, 0);
        when(tableRepository.save(any(Table.class))).thenReturn(table);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        //then
        TableDto tableDto = tableService.deleteProductsFromTableOrder(1L, productDeleteFromTable.getProducts());
        //when
        assertNotNull(tableDto);
        assertEquals(4, tableDto.seats());
        assertEquals(0, tableDto.availableSeats());
        assertEquals(TableStatus.OCCUPIED_WITH_PRODUCTS, tableDto.tableStatus());
        assertEquals(1000, tableDto.valueOfTheBill());
        assertEquals(1, tableDto.productsOnTable().getProducts().stream().mapToInt(ProductOnTable::getQuantity).findFirst().getAsInt());
        assertEquals(productsAddToTable.getProducts(), tableDto.productsOnTable().getProducts());
    }

    @Test
    void shouldThrowExceptionWhenDeletingProductsFromNonExistingTable() {
        ProductOnTable product1 = new ProductOnTable(1L, 1000, 2, "Pepsi");
        ProductsOnTable productDeleteFromTable = new ProductsOnTable(new HashSet<>(List.of(product1)));
        //when
        assertThrows(TableNotExistsException.class, () -> tableService.deleteProductsFromTableOrder(2L, productDeleteFromTable.getProducts()));
    }


}


