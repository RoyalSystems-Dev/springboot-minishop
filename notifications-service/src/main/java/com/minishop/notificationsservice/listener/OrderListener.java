package com.minishop.notificationsservice.listener;

import com.minishop.notificationsservice.dto.OrderDto;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class OrderListener {

    @Bean
    public Consumer<OrderDto> orderCreated() {
        return order -> {
            System.out.println("💌 Nueva orden recibida para notificación:");
            System.out.println("ID: " + order.getId());
            System.out.println("Producto: " + order.getProductName());
            System.out.println("Cantidad: " + order.getQuantity());
        };
    }
}
