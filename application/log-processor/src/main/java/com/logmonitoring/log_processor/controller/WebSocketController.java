package com.logmonitoring.log_processor.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")  // 클라이언트가 /app/hello로 메시지 전송
    @SendTo("/topic/greetings")  // /topic/greetings 구독자들에게 응답
    public String greeting(String message) {
        return "Hello, " + message + "!";
    }
}