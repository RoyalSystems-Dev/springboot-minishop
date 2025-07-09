package com.minishop.productsservice.service;

import com.minishop.productsservice.dto.ProductEvent;
import com.minishop.productsservice.dto.OrderEvent;
import com.minishop.productsservice.dto.NotificationMessage;
import io.nats.client.Message;
import io.nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

/**
 * Servicio que maneja los eventos y mensajería de productos usando NATS
 */
@Service
public class ProductEventService {
    
    @Autowired
    private NatsService natsService;
    
    @PostConstruct
    public void initializeSubscriptions() {
        // Suscribirse a eventos de órdenes para manejar inventario
        subscribeToOrderEvents();
    }
    
    /**
     * Publica un evento cuando se crea un producto
     */
    public void publishProductCreated(ProductEvent productEvent) {
        String subject = natsService.getProductsSubjects().getCreated();
        natsService.publish(subject, productEvent);
    }
    
    /**
     * Publica un evento cuando se actualiza un producto
     */
    public void publishProductUpdated(ProductEvent productEvent) {
        String subject = natsService.getProductsSubjects().getUpdated();
        natsService.publish(subject, productEvent);
    }
    
    /**
     * Publica un evento cuando se elimina un producto
     */
    public void publishProductDeleted(ProductEvent productEvent) {
        String subject = natsService.getProductsSubjects().getDeleted();
        natsService.publish(subject, productEvent);
    }
    
    /**
     * Publica un evento de stock bajo
     */
    public void publishLowStock(ProductEvent productEvent) {
        String subject = natsService.getProductsSubjects().getLowStock();
        natsService.publish(subject, productEvent);
        
        // También enviar notificación a administradores
        sendLowStockNotification(productEvent);
    }
    
    /**
     * Envía notificación de stock bajo
     */
    private void sendLowStockNotification(ProductEvent productEvent) {
        try {
            // Crear mensaje de notificación
            NotificationMessage notification = new NotificationMessage();
            notification.setUserId("admin");
            notification.setMessage("Stock bajo detectado para el producto: " + productEvent.getName());
            notification.setType("LOW_STOCK");
            notification.setChannel("EMAIL");
            
            String subject = natsService.getNotificationsSubjects().getSend();
            natsService.publish(subject, notification);
        } catch (Exception e) {
            System.err.println("[PRODUCTS-SERVICE] Error enviando notificación de stock bajo: " + e.getMessage());
        }
    }
    
    /**
     * Suscribe a eventos de órdenes para manejar inventario
     */
    private void subscribeToOrderEvents() {
        // Suscribirse a órdenes creadas para verificar inventario
        Subscription orderCreatedSub = natsService.subscribe(
            natsService.getOrdersSubjects().getCreated()
        );
        
        // Procesar órdenes creadas en un hilo separado
        new Thread(() -> {
            try {
                while (!orderCreatedSub.isActive()) {
                    Thread.sleep(100);
                }
                
                while (orderCreatedSub.isActive()) {
                    Message msg = orderCreatedSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handleOrderCreated(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[PRODUCTS-SERVICE] Order created subscription interrupted: " + e.getMessage());
            }
        }).start();
        
        // Suscribirse a órdenes canceladas para restaurar inventario
        Subscription orderCancelledSub = natsService.subscribe(
            natsService.getOrdersSubjects().getCancelled()
        );
        
        // Procesar órdenes canceladas en un hilo separado
        new Thread(() -> {
            try {
                while (!orderCancelledSub.isActive()) {
                    Thread.sleep(100);
                }
                
                while (orderCancelledSub.isActive()) {
                    Message msg = orderCancelledSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handleOrderCancelled(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[PRODUCTS-SERVICE] Order cancelled subscription interrupted: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Maneja eventos de órdenes creadas
     */
    private void handleOrderCreated(Message message) {
        try {
            OrderEvent orderEvent = natsService.deserializeMessage(message, OrderEvent.class);
            System.out.println("[PRODUCTS-SERVICE] Processing order created: " + orderEvent.getOrderId());
            
            // Aquí implementarías la lógica de verificación de inventario
            // Por ejemplo:
            // 1. Verificar stock disponible
            // 2. Reservar productos
            // 3. Actualizar inventario
            // 4. Publicar evento de inventario actualizado
            
            // Simulación de actualización de inventario
            ProductEvent inventoryEvent = new ProductEvent();
            inventoryEvent.setProductId("product-from-order");
            inventoryEvent.setAction("INVENTORY_RESERVED");
            inventoryEvent.setData(orderEvent);
            
            String subject = natsService.getProductsSubjects().getInventoryUpdate();
            natsService.publish(subject, inventoryEvent);
            
        } catch (Exception e) {
            System.err.println("[PRODUCTS-SERVICE] Error handling order created: " + e.getMessage());
        }
    }
    
    /**
     * Maneja eventos de órdenes canceladas
     */
    private void handleOrderCancelled(Message message) {
        try {
            OrderEvent orderEvent = natsService.deserializeMessage(message, OrderEvent.class);
            System.out.println("[PRODUCTS-SERVICE] Processing order cancelled: " + orderEvent.getOrderId());
            
            // Aquí implementarías la lógica de restauración de inventario
            // Por ejemplo:
            // 1. Liberar productos reservados
            // 2. Restaurar stock disponible
            // 3. Publicar evento de inventario restaurado
            
            // Simulación de restauración de inventario
            ProductEvent inventoryEvent = new ProductEvent();
            inventoryEvent.setProductId("product-from-cancelled-order");
            inventoryEvent.setAction("INVENTORY_RESTORED");
            inventoryEvent.setData(orderEvent);
            
            String subject = natsService.getProductsSubjects().getInventoryUpdate();
            natsService.publish(subject, inventoryEvent);
            
        } catch (Exception e) {
            System.err.println("[PRODUCTS-SERVICE] Error handling order cancelled: " + e.getMessage());
        }
    }
}
