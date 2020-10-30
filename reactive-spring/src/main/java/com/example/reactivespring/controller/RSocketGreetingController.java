package com.example.reactivespring.controller;

import com.example.reactivespring.configuration.IntervalMessageProducer;
import com.example.reactivespring.entity.GreetingRequest;
import com.example.reactivespring.entity.GreetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class RSocketGreetingController {

    private final IntervalMessageProducer messageProducer;

    @MessageMapping(value = "greetings")
    Flux<GreetingResponse> greet(GreetingRequest request) {
        return messageProducer.produce(request);
    }

}
