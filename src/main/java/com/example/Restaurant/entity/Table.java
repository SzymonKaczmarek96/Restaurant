package com.example.Restaurant.entity;

import com.example.Restaurant.dto.TableDto;
import com.example.Restaurant.exceptions.InvalidTableStatusTransitionException;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Table(name = "tables")
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    @Setter
    private Long tableId;

    @Column(name = "seats", nullable = false)
    @Setter
    private int seats;

    @Column(name = "available_seats", nullable = false)
    @Setter
    private int availableSeats;

    @Column(name = "products_on_table", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    @Setter
    private ProductsOnTable productsOnTable;

    @Enumerated(EnumType.STRING)
    @Column(name = "table_status", nullable = false)
    @Setter
    private TableStatus tableStatus;

    @JsonProperty("value_of_bill")
    @Column(name = "value_of_the_bill", nullable = false)
    private int valueOfTheBill;

    public TableDto tableToDto() {
        TableDto tableDto = new TableDto(seats, availableSeats, productsOnTable, tableStatus, valueOfTheBill);
        return tableDto;
    }

    public Table(int seats) {
        this.seats = seats;
    }

    public Table(TableStatus tableStatus) {
        this.tableStatus = tableStatus;
    }

    public Table(Long tableId, int seats, int availableSeats, ProductsOnTable productsOnTable, TableStatus tableStatus) {
        this.tableId = tableId;
        this.seats = seats;
        this.availableSeats = availableSeats;
        this.productsOnTable = productsOnTable;
        this.tableStatus = tableStatus;
    }

    public void setTableStatus(TableStatus newStatus) {
        if (this.tableStatus == TableStatus.PAID || this.tableStatus == TableStatus.OCCUPIED || this.tableStatus == TableStatus.FREE) {
            this.tableStatus = newStatus;
        } else {
            throw new InvalidTableStatusTransitionException("Table can only be freed from PAID or OCCUPIED status.");
        }
    }

    public void setProductsOnTable(ProductsOnTable productsOnTable) {
        if (productsOnTable.getProducts().isEmpty()) {
            valueOfTheBill = 0;
            tableStatus = TableStatus.PAID;
        } else {
            valueOfTheBill = productsOnTable.getProducts().stream()
                    .map(product -> product.getQuantity() * product.getPrice())
                    .toList().stream().mapToInt(Integer::intValue).sum();
            tableStatus = TableStatus.OCCUPIED_WITH_PRODUCTS;
        }
        this.productsOnTable = productsOnTable;
    }

}
