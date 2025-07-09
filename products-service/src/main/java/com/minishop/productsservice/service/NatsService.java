package com.minishop.productsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minishop.productsservice.config.MessagingProperties;
import com.minishop.productsservice.config.NatsProperties;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio base para operaciones con NATS en products-service
 */
@Service
public class NatsService {
    
    @Autowired
    private Connection natsConnection;
    
    @Autowired
    private NatsProperties natsProperties;
    
    @Autowired
    private MessagingProperties messagingProperties;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Publica un mensaje en un subject específico
     */
    public void publish(String subject, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            natsConnection.publish(subject, jsonMessage.getBytes(StandardCharsets.UTF_8));
            System.out.println("[PRODUCTS-SERVICE] Published to " + subject + ": " + jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing message to JSON", e);
        }
    }
    
    /**
     * Publica un mensaje y espera respuesta
     */
    public <T> CompletableFuture<T> request(String subject, Object message, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                Message reply = natsConnection.request(subject, 
                    jsonMessage.getBytes(StandardCharsets.UTF_8), 
                    Duration.ofMillis(natsProperties.getProducer().getTimeout()));
                
                if (reply != null) {
                    String responseJson = new String(reply.getData(), StandardCharsets.UTF_8);
                    return objectMapper.readValue(responseJson, responseType);
                }
                return null;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing JSON", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted", e);
            } catch (Exception e) {
                throw new RuntimeException("Request failed", e);
            }
        });
    }
    
    /**
     * Suscribe a un subject
     */
    public Subscription subscribe(String subject) {
        return natsConnection.subscribe(subject);
    }
    
    /**
     * Obtiene los subjects de productos
     */
    public MessagingProperties.Products getProductsSubjects() {
        return messagingProperties.getSubjects().getProducts();
    }
    
    /**
     * Obtiene los subjects de órdenes
     */
    public MessagingProperties.Orders getOrdersSubjects() {
        return messagingProperties.getSubjects().getOrders();
    }
    
    /**
     * Obtiene los subjects de notificaciones
     */
    public MessagingProperties.Notifications getNotificationsSubjects() {
        return messagingProperties.getSubjects().getNotifications();
    }
    
    /**
     * Deserializa un mensaje NATS
     */
    public <T> T deserializeMessage(Message message, Class<T> targetType) {
        try {
            String jsonMessage = new String(message.getData(), StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonMessage, targetType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing message", e);
        }
    }
}
