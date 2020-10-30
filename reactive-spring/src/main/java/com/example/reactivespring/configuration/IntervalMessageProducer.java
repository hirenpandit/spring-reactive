package com.example.reactivespring.configuration;

import com.example.reactivespring.entity.GreetingRequest;
import com.example.reactivespring.entity.GreetingResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Component
public class IntervalMessageProducer {
    public Flux<GreetingResponse> produce(GreetingRequest request) {
        return Flux
                .fromStream(Stream.generate(() -> "Hello, "+request.getName()+" @ "+ Instant.now()))
                .map(GreetingResponse::new)
                .delayElements(Duration.ofSeconds(1));
    }
}
