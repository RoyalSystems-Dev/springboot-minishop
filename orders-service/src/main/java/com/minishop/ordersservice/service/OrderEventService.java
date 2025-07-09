package com.minishop.ordersservice.service;

import com.minishop.ordersservice.dto.OrderEvent;
import com.minishop.ordersservice.dto.NotificationMessage;
import io.nats.client.Message;
import io.nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

/**
 * Servicio que maneja los eventos y mensajería de órdenes usando NATS
 */
@Service
public class OrderEventService {
    
    @Autowired
    private NatsService natsService;
    
    @PostConstruct
    public void initializeSubscriptions() {
        // Suscribirse a respuestas de pagos
        subscribeToPaymentEvents();
    }
    
    /**
     * Publica un evento cuando se crea una orden
     */
    public void publishOrderCreated(OrderEvent orderEvent) {
        String subject = natsService.getOrdersSubjects().getCreated();
        natsService.publish(subject, orderEvent);
        
        // También enviar notificación
        sendOrderNotification(orderEvent, "Nueva orden creada: " + orderEvent.getOrderId());
    }
    
    /**
     * Publica un evento cuando se actualiza una orden
     */
    public void publishOrderUpdated(OrderEvent orderEvent) {
        String subject = natsService.getOrdersSubjects().getUpdated();
        natsService.publish(subject, orderEvent);
    }
    
    /**
     * Publica un evento cuando se cancela una orden
     */
    public void publishOrderCancelled(OrderEvent orderEvent) {
        String subject = natsService.getOrdersSubjects().getCancelled();
        natsService.publish(subject, orderEvent);
        
        // También enviar notificación
        sendOrderNotification(orderEvent, "Orden cancelada: " + orderEvent.getOrderId());
    }
    
    /**
     * Publica un evento cuando cambia el estado de una orden
     */
    public void publishOrderStatusChanged(OrderEvent orderEvent) {
        String subject = natsService.getOrdersSubjects().getStatusChanged();
        natsService.publish(subject, orderEvent);
    }
    
    /**
     * Envía una notificación usando NATS
     */
    private void sendOrderNotification(OrderEvent orderEvent, String message) {
        NotificationMessage notification = new NotificationMessage();
        notification.setUserId(orderEvent.getUserId());
        notification.setMessage(message);
        notification.setType("ORDER");
        notification.setTimestamp(orderEvent.getTimestamp());
        
        String subject = natsService.getNotificationsSubjects().getSend();
        natsService.publish(subject, notification);
    }
    
    /**
     * Suscribe a eventos de pagos
     */
    private void subscribeToPaymentEvents() {
        // Suscribirse a confirmaciones de pago
        Subscription paymentConfirmedSub = natsService.subscribe(
            natsService.getPaymentsSubjects().getConfirmed()
        );
        
        // Procesar mensajes de pago confirmado en un hilo separado
        new Thread(() -> {
            try {
                while (!paymentConfirmedSub.isActive()) {
                    Thread.sleep(100);
                }
                
                while (paymentConfirmedSub.isActive()) {
                    Message msg = paymentConfirmedSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handlePaymentConfirmed(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Payment subscription interrupted: " + e.getMessage());
            }
        }).start();
        
        // Suscribirse a fallos de pago
        Subscription paymentFailedSub = natsService.subscribe(
            natsService.getPaymentsSubjects().getFailed()
        );
        
        // Procesar mensajes de pago fallido en un hilo separado
        new Thread(() -> {
            try {
                while (!paymentFailedSub.isActive()) {
                    Thread.sleep(100);
                }
                
                while (paymentFailedSub.isActive()) {
                    Message msg = paymentFailedSub.nextMessage(Duration.ofSeconds(1));
                    if (msg != null) {
                        handlePaymentFailed(msg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Payment failed subscription interrupted: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Maneja eventos de pago confirmado
     */
    private void handlePaymentConfirmed(Message message) {
        try {
            // Aquí procesarías el evento de pago confirmado
            System.out.println("Payment confirmed received: " + new String(message.getData()));
            
            // Ejemplo: actualizar el estado de la orden
            // OrderEvent orderEvent = natsService.deserializeMessage(message, OrderEvent.class);
            // updateOrderStatus(orderEvent.getOrderId(), "PAID");
            
        } catch (Exception e) {
            System.err.println("Error handling payment confirmed: " + e.getMessage());
        }
    }
    
    /**
     * Maneja eventos de pago fallido
     */
    private void handlePaymentFailed(Message message) {
        try {
            // Aquí procesarías el evento de pago fallido
            System.out.println("Payment failed received: " + new String(message.getData()));
            
            // Ejemplo: cancelar la orden o marcarla como pago fallido
            // OrderEvent orderEvent = natsService.deserializeMessage(message, OrderEvent.class);
            // updateOrderStatus(orderEvent.getOrderId(), "PAYMENT_FAILED");
            
        } catch (Exception e) {
            System.err.println("Error handling payment failed: " + e.getMessage());
        }
    }
}
