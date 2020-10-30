package com.example.springrsocketclient;

import com.example.springrsocketclient.entity.GreetingRequest;
import com.example.springrsocketclient.entity.GreetingResponse;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GreetingController {

    private final RSocketRequester requester;

    @GetMapping(value = "/greetings/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<GreetingResponse> greetingResponsePublisher(@PathVariable String name) {
        return requester.route("greetings")
                .data(new GreetingRequest(name))
                .retrieveFlux(GreetingResponse.class);
    }

}
