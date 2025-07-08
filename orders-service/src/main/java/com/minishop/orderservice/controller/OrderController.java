package com.minishop.orderservice.controller;

import com.minishop.orderservice.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final StreamBridge streamBridge;

    @PostMapping
    public String createOrder(@RequestBody OrderDto order) {
        streamBridge.send("orderCreated-out-0", order);
        return "Order published to NATS!";
    }
}
