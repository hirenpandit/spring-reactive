package com.example.reactivespring.repository;

import com.example.reactivespring.entity.Reservation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {
}
