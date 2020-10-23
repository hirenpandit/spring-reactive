package com.example.reactivespring.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "reservation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Reservation {
    String id;
    String name;
}
