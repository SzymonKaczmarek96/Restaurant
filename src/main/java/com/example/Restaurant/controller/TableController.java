package com.example.Restaurant.controller;

import com.example.Restaurant.dto.TableDto;
import com.example.Restaurant.entity.ProductOnTable;
import com.example.Restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/table")
public class TableController {

    private TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableDto>> tableList() {
        List<TableDto> createdList = tableService.getAllTables();
        return ResponseEntity.ok(createdList);
    }

    @GetMapping("/status/{tableStatus}")
    public ResponseEntity<List<TableDto>> tableStatusList(@PathVariable String tableStatus) {
        List<TableDto> checkedTableStatusList = tableService.getAllTablesByStatus(tableStatus);
        return ResponseEntity.ok(checkedTableStatusList);
    }

    @PostMapping("/create")
    public ResponseEntity<TableDto> createTable(@RequestBody int seats){
        TableDto createdTable = tableService.createTable(seats);
        return ResponseEntity.ok().body(createdTable);
    }

    @PutMapping("/open")
    public ResponseEntity<TableDto> openPaidTable(@RequestParam("id") Long id) {
        TableDto changedTableStatus = tableService.openTable(id);
        return ResponseEntity.ok(changedTableStatus);
    }

    @PutMapping("/close")
    public ResponseEntity<TableDto> closePaidTable(@RequestParam("id") Long id) {
        TableDto changedTableStatus = tableService.closeTable(id);
        return ResponseEntity.ok(changedTableStatus);
    }

    @PutMapping("/reservation")
    public ResponseEntity<TableDto> tableReservation(@RequestParam("id") Long id, @RequestBody int howManyPeople) {
        validateNumberOfPeople(howManyPeople);
        TableDto changedTableStatus = tableService.reservationTable(id, howManyPeople);
        return ResponseEntity.ok().body(changedTableStatus);
    }

    @PutMapping("/cancel")
    public ResponseEntity<TableDto> cancelOccupied(@RequestParam("id") Long id) {
        TableDto changedTableStatus = tableService.cancelReservation(id);
        return ResponseEntity.ok(changedTableStatus);
    }

    @PutMapping("/order")
    public ResponseEntity<TableDto> changeOrder(@RequestParam("id") Long id,@RequestBody Set<ProductOnTable> productList) {
        TableDto changedOrder = tableService.addProductsToTable(id, productList);
        return ResponseEntity.ok().body(changedOrder);
    }

    @PutMapping("/delete")
    public ResponseEntity<TableDto> deleteOrderList(@RequestParam("id") Long id, @RequestBody Set<ProductOnTable> productList) {
        TableDto deleteProductsToTableOrderOrder = tableService.deleteProductsFromTableOrder(id, productList);
        return ResponseEntity.ok().body(deleteProductsToTableOrderOrder);
    }


    private void validateNumberOfPeople(int howManyPeople) {
        if (howManyPeople < 0) {
            throw new IllegalArgumentException("Number of people must be zero or positive.");
        }
    }
}
