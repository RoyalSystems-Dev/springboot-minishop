package com.minishop.ordersservice.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.minishop.ordersservice.dto.OrderDto;
import com.minishop.ordersservice.dto.OrderEvent;
import com.minishop.ordersservice.service.OrderEventService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderEventService orderEventService;

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

        // Publicar evento de orden creada usando NATS
        OrderEvent orderEvent = new OrderEvent(
            orderDto.getId().toString(), 
            "user-" + orderDto.getId(), // Simulando user ID
            "CREATED", 
            "CREATE"
        );
        orderEvent.setData(orderDto);
        orderEventService.publishOrderCreated(orderEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        if (orders.containsKey(id)) {
            orderDto.setId(id);
            orders.put(id, orderDto);
            
            // Publicar evento de orden actualizada usando NATS
            OrderEvent orderEvent = new OrderEvent(
                orderDto.getId().toString(), 
                "user-" + orderDto.getId(), // Simulando user ID
                "UPDATED", 
                "UPDATE"
            );
            orderEvent.setData(orderDto);
            orderEventService.publishOrderUpdated(orderEvent);
            
            return ResponseEntity.ok(orderDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orders.containsKey(id)) {
            OrderDto orderDto = orders.get(id);
            orders.remove(id);
            
            // Publicar evento de orden cancelada usando NATS
            OrderEvent orderEvent = new OrderEvent(
                id.toString(), 
                "user-" + id, // Simulando user ID
                "CANCELLED", 
                "DELETE"
            );
            orderEvent.setData(orderDto);
            orderEventService.publishOrderCancelled(orderEvent);
            
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
    
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @PathVariable String status) {
        if (orders.containsKey(id)) {
            OrderDto orderDto = orders.get(id);
            
            // Publicar evento de cambio de estado usando NATS
            OrderEvent orderEvent = new OrderEvent(
                id.toString(), 
                "user-" + id, // Simulando user ID
                status, 
                "STATUS_CHANGE"
            );
            orderEvent.setData(orderDto);
            orderEventService.publishOrderStatusChanged(orderEvent);
            
            return ResponseEntity.ok(orderDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
