package com.example.Restaurant.entity;

import com.example.Restaurant.dto.TableDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @Column(name= "table_id")
    private Long tableId;

    @Column( name = "seats",nullable = false)
    private int seats;

    @Column(name = "available_seats",nullable = false)
    private int availableSeats;

    @Column(name = "products_on_table",nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ProductsOnTable productsOnTable;

    @Enumerated(EnumType.STRING)
    @Column(name = "table_status", nullable = false)
    private TableStatus tableStatus;


    public TableDto tableToDto(){
        TableDto tableDto = new TableDto(seats,availableSeats,productsOnTable,tableStatus);
        return tableDto;
    }
}
