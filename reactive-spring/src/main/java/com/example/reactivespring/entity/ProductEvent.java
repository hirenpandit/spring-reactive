package com.example.reactivespring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductEvent {
    private Long val;
    private String productEvent;

}
