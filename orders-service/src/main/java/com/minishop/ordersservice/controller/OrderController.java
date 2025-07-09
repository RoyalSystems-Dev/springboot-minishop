package com.minishop.ordersservice.controller;

import org.springframework.web.bind.annotation.*;

import com.minishop.ordersservice.dto.OrderDto;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/orders")
public class OrderController {

    // Simulación de base de datos en memoria
    private static final ConcurrentHashMap<Long, OrderDto> orders = new ConcurrentHashMap<>();
    private static long nextId = 1L;

    // Inicializar datos al cargar la clase
    static {
        initializeOrders();
    }

    private static void initializeOrders() {
        for (int i = 1; i <= 3; i++) {
            OrderDto order = new OrderDto();
            order.setId((long) i);
            order.setProductName("Product-" + i);
            order.setQuantity(i * 2);

            orders.put((long) i, order);
            nextId = i + 1;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        OrderDto order = orders.get(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        orderDto.setId(nextId++);

        orders.put(orderDto.getId(), orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        if (orders.containsKey(id)) {
            orderDto.setId(id);
            orders.put(id, orderDto);
            return ResponseEntity.ok(orderDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orders.containsKey(id)) {
            orders.remove(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/product/{productName}")
    public List<OrderDto> getOrdersByProduct(@PathVariable String productName) {
        return orders.values().stream()
                .filter(order -> order.getProductName().equalsIgnoreCase(productName))
                .collect(ArrayList::new, (list, order) -> list.add(order), ArrayList::addAll);
    }
}
