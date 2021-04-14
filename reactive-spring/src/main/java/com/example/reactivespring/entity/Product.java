package com.example.reactivespring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Product {

    private String id;
    private String name;
    private double price;

}
