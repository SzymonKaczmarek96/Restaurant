package com.example.Restaurant.exceptions;

import com.example.Restaurant.entity.TableStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TableStatusRequiredException extends RuntimeException{

    public TableStatusRequiredException(TableStatus tableStatus){
        super("Status can only be changed for " + tableStatus);
    }
}
