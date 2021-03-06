package com.example.reactivespring.controller;

import com.example.reactivespring.configuration.IntervalMessageProducer;
import com.example.reactivespring.entity.GreetingRequest;
import com.example.reactivespring.entity.GreetingResponse;
import com.example.reactivespring.entity.Reservation;
import com.example.reactivespring.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final IntervalMessageProducer messageProducer;

    @GetMapping("/reservations")
    Publisher<Reservation> reservationPublisher(){
        return this.reservationRepository.findAll();
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/see/{name}")
    Publisher<GreetingResponse> greeting(@PathVariable String name) {
        return this.messageProducer.produce(new GreetingRequest(name));
    }

}

