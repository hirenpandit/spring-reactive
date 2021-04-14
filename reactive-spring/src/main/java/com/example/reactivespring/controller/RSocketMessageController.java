package com.example.reactivespring.controller;

import com.example.reactivespring.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class RSocketMessageController {

    @MessageMapping("request-response")
    Message requestResponse(Message request) {
      log.info("Received request-response request: {}", request);
      return new Message("server", "response");
    }

}
