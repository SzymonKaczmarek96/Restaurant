package com.example.Restaurant.repository;

import com.example.Restaurant.entity.Table;
import com.example.Restaurant.entity.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//Crud repository

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    List<Table> findByTableStatus(TableStatus tableStatus);
}
