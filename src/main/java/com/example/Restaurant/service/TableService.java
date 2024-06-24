package com.example.Restaurant.service;


import com.example.Restaurant.dto.TableDto;
import com.example.Restaurant.entity.*;
import com.example.Restaurant.exceptions.TableNotExistsException;
import com.example.Restaurant.exceptions.TableStatusNotExistsException;
import com.example.Restaurant.repository.ProductRepository;
import com.example.Restaurant.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TableService {

    private TableRepository tableRepository;

    private ProductRepository productRepository;

    @Autowired
    public TableService(TableRepository tableRepository, ProductRepository productRepository) {
        this.tableRepository = tableRepository;
        this.productRepository = productRepository;
    }

    public List<TableDto> getAllTables() {
        return tableRepository.findAll().stream().map(Table::tableToDto).toList();
    }


    public List<TableDto> getAllTablesByStatus(String tableStatus) {
        String convertedString = convertString(tableStatus);
        checkTableStatus(convertedString);
        List<TableDto> findAllFreeTables = tableRepository.findByTableStatus(TableStatus.valueOf(convertedString)).stream().map(Table::tableToDto).toList();
        return findAllFreeTables;
    }

    public TableDto createTable(int seats) {
        checkIntroducedSeats(seats);
        Table createdTable = new Table(seats);
        createdTable.setAvailableSeats(seats);
        createdTable.setProductsOnTable(new ProductsOnTable());
        createdTable.setTableStatus(TableStatus.FREE);
        return tableRepository.save(createdTable).tableToDto();
    }

    public TableDto openTable(Long id) {
        Table findTableToOpen = tableRepository.findById(id).orElseThrow(() -> new TableNotExistsException());
        findTableToOpen.setAvailableSeats(findTableToOpen.getSeats());
        findTableToOpen.setTableStatus(TableStatus.FREE);
        return tableRepository.save(findTableToOpen).tableToDto();
    }

    public TableDto closeTable(Long id) {
        Table findTableToClose = tableRepository.findById(id).orElseThrow(() -> new TableNotExistsException());
        findTableToClose.setProductsOnTable(new ProductsOnTable(new HashSet<>()));
        return tableRepository.save(findTableToClose).tableToDto();
    }

    @Transactional
    public TableDto reservationTable(Long id, int howManyPeoples) {
        Table findTableToReservation = tableRepository.findById(id).orElseThrow(() -> new TableNotExistsException());
        checkTheNumberOfSeats(findTableToReservation.getSeats(), howManyPeoples);
        findTableToReservation.setTableStatus(TableStatus.OCCUPIED);
        findTableToReservation.setAvailableSeats(findTableToReservation.getSeats() - howManyPeoples);
        return tableRepository.save(findTableToReservation).tableToDto();
    }

    public TableDto cancelReservation(Long id) {
        Table findTableToCancelReservation = tableRepository.findById(id).orElseThrow(() -> new TableNotExistsException());
        findTableToCancelReservation.setAvailableSeats(findTableToCancelReservation.getSeats());
        findTableToCancelReservation.setTableStatus(TableStatus.FREE);
        return tableRepository.save(findTableToCancelReservation).tableToDto();
    }

    @Transactional
    public TableDto addProductsToTable(Long id, Set<ProductOnTable> productList) {
        Table findTableToModifyOrder = tableRepository.findById(id).orElseThrow(() -> new TableNotExistsException());
        Set<ProductOnTable> modifiedProductsOnTable = addToList(findTableToModifyOrder.getProductsOnTable().getProducts(), productList);
        findTableToModifyOrder.setProductsOnTable(new ProductsOnTable(modifiedProductsOnTable));
        return tableRepository.save(findTableToModifyOrder).tableToDto();
    }

    @Transactional
    public TableDto deleteProductsFromTableOrder(Long id, Set<ProductOnTable> productToDelete) {
        Table findTableToModifyProductOnTable = tableRepository.findById(id).orElseThrow(() -> new TableNotExistsException());
        Set<ProductOnTable> deleteProductOnTable = deleteProductInList
                (findTableToModifyProductOnTable.getProductsOnTable().getProducts(), productToDelete);
        findTableToModifyProductOnTable.setProductsOnTable(new ProductsOnTable(deleteProductOnTable));
        return tableRepository.save(findTableToModifyProductOnTable).tableToDto();
    }
    private void checkTableStatus(String tableStatus) {
        try {
            TableStatus.valueOf(tableStatus);
        } catch (IllegalArgumentException e) {
            throw new TableStatusNotExistsException(tableStatus);
        }
    }
    private String convertString(String tableStatus) {
        String upperCase = tableStatus.toUpperCase();
        return upperCase;
    }

    private void checkTheNumberOfSeats(int availableSeats, int peoples) {
        if (availableSeats < peoples) {
            throw new IllegalArgumentException();
        }
    }
    private void checkIntroducedSeats(int seats){
        if(seats <= 0){
            throw new IllegalArgumentException("Seats must be bigger than 0");
        }
    }

    private Set<ProductOnTable> addToList(Set<ProductOnTable> existingProductList, Set<ProductOnTable> requestList) {
        for (ProductOnTable product : requestList) {
            Optional<ProductOnTable> findProductById = existingProductList.stream()
                    .filter(requestProduct -> product.getProductId().equals(requestProduct.getProductId()))
                    .findFirst();
            if (findProductById.isPresent()) {
                findProductById.get().setQuantity(product.getQuantity() + findProductById.get().getQuantity());
            } else {
                existingProductList.add(product);
            }
        }
        return existingProductList;
    }

    private Set<ProductOnTable> deleteProductInList(Set<ProductOnTable> existingProductList, Set<ProductOnTable> requestList) {
        for (ProductOnTable product : requestList) {
            Optional<ProductOnTable> findProductById = existingProductList.stream()
                    .filter(requestProduct -> product.getProductId().equals(requestProduct.getProductId())).findFirst();
            if (findProductById.isPresent()) {
                if (product.getQuantity() >= findProductById.get().getQuantity()) {
                    existingProductList.removeIf(productToDelete -> product.getProductId().equals(productToDelete.getProductId()));
                } else{
                    findProductById.get().setQuantity(findProductById.get().getQuantity() - product.getQuantity());
                }
            }
        }
        return existingProductList;
    }

}

