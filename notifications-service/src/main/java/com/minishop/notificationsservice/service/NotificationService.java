package com.minishop.notificationsservice.service;

import com.minishop.notificationsservice.config.MessagingProperties;
import com.minishop.notificationsservice.config.NotificationProperties;
import com.minishop.notificationsservice.model.Notification;
import com.minishop.notificationsservice.repository.NotificationRepository;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Servicio principal de notificaciones que escucha eventos NATS
 */
@Service
public class NotificationService {
    
    @Autowired
    private Connection natsConnection;
    
    @Autowired
    private MessagingProperties messagingProperties;
    
    @Autowired
    private NotificationProperties notificationProperties;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @PostConstruct
    public void initializeSubscriptions() {
        System.out.println("[NOTIFICATIONS-SERVICE] Initializing NATS subscriptions...");
        
        // Suscribirse a todos los eventos relevantes
        subscribeToNotificationEvents();
        subscribeToOrderEvents();
        subscribeToProductEvents();
        subscribeToPaymentEvents();
    }
    
    /**
     * Suscribe a eventos de notificaciones directas
     */
    private void subscribeToNotificationEvents() {
        Subscription notificationSub = natsConnection.subscribe(
            messagingProperties.getSubjects().getNotifications().getSend()
        );
        
        new Thread(() -> {
            try {
                while (!notificationSub.isActive()) {
                    Thread.sleep(100);
                }
                
                System.out.println("[NOTIFICATIONS-SERVICE] Listening to: " + 
                    messagingProperties.getSubjects().getNotifications().getSend());
                
                while (notificationSub.isActive()) {
                    Message msg = notificationSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handleNotificationRequest(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[NOTIFICATIONS-SERVICE] Notification subscription interrupted: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Suscribe a eventos de órdenes
     */
    private void subscribeToOrderEvents() {
        // Órdenes creadas
        Subscription orderCreatedSub = natsConnection.subscribe(
            messagingProperties.getSubjects().getOrders().getCreated()
        );
        
        new Thread(() -> {
            try {
                while (!orderCreatedSub.isActive()) {
                    Thread.sleep(100);
                }
                
                System.out.println("[NOTIFICATIONS-SERVICE] Listening to: " + 
                    messagingProperties.getSubjects().getOrders().getCreated());
                
                while (orderCreatedSub.isActive()) {
                    Message msg = orderCreatedSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handleOrderCreated(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[NOTIFICATIONS-SERVICE] Order created subscription interrupted: " + e.getMessage());
            }
        }).start();
        
        // Órdenes canceladas
        Subscription orderCancelledSub = natsConnection.subscribe(
            messagingProperties.getSubjects().getOrders().getCancelled()
        );
        
        new Thread(() -> {
            try {
                while (!orderCancelledSub.isActive()) {
                    Thread.sleep(100);
                }
                
                System.out.println("[NOTIFICATIONS-SERVICE] Listening to: " + 
                    messagingProperties.getSubjects().getOrders().getCancelled());
                
                while (orderCancelledSub.isActive()) {
                    Message msg = orderCancelledSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handleOrderCancelled(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[NOTIFICATIONS-SERVICE] Order cancelled subscription interrupted: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Suscribe a eventos de productos
     */
    private void subscribeToProductEvents() {
        Subscription lowStockSub = natsConnection.subscribe(
            messagingProperties.getSubjects().getProducts().getLowStock()
        );
        
        new Thread(() -> {
            try {
                while (!lowStockSub.isActive()) {
                    Thread.sleep(100);
                }
                
                System.out.println("[NOTIFICATIONS-SERVICE] Listening to: " + 
                    messagingProperties.getSubjects().getProducts().getLowStock());
                
                while (lowStockSub.isActive()) {
                    Message msg = lowStockSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handleLowStock(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[NOTIFICATIONS-SERVICE] Low stock subscription interrupted: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Suscribe a eventos de pagos
     */
    private void subscribeToPaymentEvents() {
        // Pagos confirmados
        Subscription paymentConfirmedSub = natsConnection.subscribe(
            messagingProperties.getSubjects().getPayments().getConfirmed()
        );
        
        new Thread(() -> {
            try {
                while (!paymentConfirmedSub.isActive()) {
                    Thread.sleep(100);
                }
                
                System.out.println("[NOTIFICATIONS-SERVICE] Listening to: " + 
                    messagingProperties.getSubjects().getPayments().getConfirmed());
                
                while (paymentConfirmedSub.isActive()) {
                    Message msg = paymentConfirmedSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handlePaymentConfirmed(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[NOTIFICATIONS-SERVICE] Payment confirmed subscription interrupted: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Maneja solicitudes directas de notificación
     */
    private void handleNotificationRequest(Message message) {
        try {
            String jsonMessage = new String(message.getData(), StandardCharsets.UTF_8);
            System.out.println("[NOTIFICATIONS-SERVICE] Notification request received: " + jsonMessage);
            
            // Aquí implementarías el envío real de la notificación
            sendNotification(jsonMessage, "DIRECT");
            
        } catch (Exception e) {
            System.err.println("[NOTIFICATIONS-SERVICE] Error handling notification request: " + e.getMessage());
        }
    }
    
    /**
     * Maneja eventos de órdenes creadas
     */
    private void handleOrderCreated(Message message) {
        try {
            String jsonMessage = new String(message.getData(), StandardCharsets.UTF_8);
            System.out.println("[NOTIFICATIONS-SERVICE] Order created event received: " + jsonMessage);
            
            sendNotification("Nueva orden creada", "ORDER_CREATED");
            
        } catch (Exception e) {
            System.err.println("[NOTIFICATIONS-SERVICE] Error handling order created: " + e.getMessage());
        }
    }
    
    /**
     * Maneja eventos de órdenes canceladas
     */
    private void handleOrderCancelled(Message message) {
        try {
            String jsonMessage = new String(message.getData(), StandardCharsets.UTF_8);
            System.out.println("[NOTIFICATIONS-SERVICE] Order cancelled event received: " + jsonMessage);
            
            sendNotification("Orden cancelada", "ORDER_CANCELLED");
            
        } catch (Exception e) {
            System.err.println("[NOTIFICATIONS-SERVICE] Error handling order cancelled: " + e.getMessage());
        }
    }
    
    /**
     * Maneja eventos de stock bajo
     */
    private void handleLowStock(Message message) {
        try {
            String jsonMessage = new String(message.getData(), StandardCharsets.UTF_8);
            System.out.println("[NOTIFICATIONS-SERVICE] Low stock event received: " + jsonMessage);
            
            sendNotification("Stock bajo detectado", "LOW_STOCK");
            
        } catch (Exception e) {
            System.err.println("[NOTIFICATIONS-SERVICE] Error handling low stock: " + e.getMessage());
        }
    }
    
    /**
     * Maneja eventos de pagos confirmados
     */
    private void handlePaymentConfirmed(Message message) {
        try {
            String jsonMessage = new String(message.getData(), StandardCharsets.UTF_8);
            System.out.println("[NOTIFICATIONS-SERVICE] Payment confirmed event received: " + jsonMessage);
            
            sendNotification("Pago confirmado", "PAYMENT_CONFIRMED");
            
        } catch (Exception e) {
            System.err.println("[NOTIFICATIONS-SERVICE] Error handling payment confirmed: " + e.getMessage());
        }
    }
    
    /**
     * Envía notificación usando el canal configurado
     */
    private void sendNotification(String message, String type) {
        System.out.println("[NOTIFICATIONS-SERVICE] Sending notification:");
        System.out.println("  Type: " + type);
        System.out.println("  Message: " + message);
        
        // Crear y guardar la notificación en el repositorio
        Notification notification = createNotificationByType(type, message);
        notificationRepository.save(notification);
        
        // Verificar qué canales están habilitados
        if (notificationProperties.getChannels().getEmail().isEnabled()) {
            sendEmailNotification(message, type);
        }
        
        if (notificationProperties.getChannels().getSms().isEnabled()) {
            sendSmsNotification(message, type);
        }
        
        if (notificationProperties.getChannels().getPush().isEnabled()) {
            sendPushNotification(message, type);
        }
    }
    
    /**
     * Crea una notificación basada en el tipo
     */
    private Notification createNotificationByType(String type, String message) {
        String title;
        String severity;
        
        switch (type) {
            case "ORDER_CREATED":
                title = "Nueva Orden";
                severity = "SUCCESS";
                break;
            case "ORDER_CANCELLED":
                title = "Orden Cancelada";
                severity = "WARNING";
                break;
            case "LOW_STOCK":
                title = "Stock Bajo";
                severity = "ERROR";
                break;
            case "PAYMENT_CONFIRMED":
                title = "Pago Confirmado";
                severity = "SUCCESS";
                break;
            case "DIRECT":
                title = "Notificación Directa";
                severity = "INFO";
                break;
            default:
                title = "Notificación";
                severity = "INFO";
                break;
        }
        
        return new Notification(type, title, message, severity);
    }
    
    /**
     * Simula envío de email
     */
    private void sendEmailNotification(String message, String type) {
        System.out.println("[NOTIFICATIONS-SERVICE] 📧 EMAIL sent: " + message);
        System.out.println("  SMTP Host: " + notificationProperties.getChannels().getEmail().getSmtpHost());
        System.out.println("  From: " + notificationProperties.getChannels().getEmail().getUsername());
    }
    
    /**
     * Simula envío de SMS
     */
    private void sendSmsNotification(String message, String type) {
        System.out.println("[NOTIFICATIONS-SERVICE] 📱 SMS sent: " + message);
        System.out.println("  Provider: " + notificationProperties.getChannels().getSms().getProvider());
    }
    
    /**
     * Simula envío de push notification
     */
    private void sendPushNotification(String message, String type) {
        System.out.println("[NOTIFICATIONS-SERVICE] 🔔 PUSH sent: " + message);
        System.out.println("  Firebase: " + notificationProperties.getChannels().getPush().getFirebaseKey());
    }
}
