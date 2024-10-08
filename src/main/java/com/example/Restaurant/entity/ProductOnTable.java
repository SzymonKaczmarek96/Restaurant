package com.example.Restaurant.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOnTable implements Serializable {
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("price")
    private int price;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("product_name")
    private String productName;

}
