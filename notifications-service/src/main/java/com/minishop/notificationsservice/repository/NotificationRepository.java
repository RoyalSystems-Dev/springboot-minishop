package com.minishop.notificationsservice.repository;

import com.minishop.notificationsservice.model.Notification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para notificaciones
 */
@Repository
public class NotificationRepository {
    
    private final ConcurrentLinkedDeque<Notification> notifications = new ConcurrentLinkedDeque<>();
    private final int MAX_NOTIFICATIONS = 1000; // Máximo número de notificaciones a mantener

    /**
     * Guarda una nueva notificación
     */
    public Notification save(Notification notification) {
        notifications.addFirst(notification);
        
        // Mantener solo las últimas MAX_NOTIFICATIONS
        while (notifications.size() > MAX_NOTIFICATIONS) {
            notifications.removeLast();
        }
        
        return notification;
    }

    /**
     * Encuentra todas las notificaciones ordenadas por timestamp (más reciente primero)
     */
    public List<Notification> findAll() {
        return new ArrayList<>(notifications);
    }

    /**
     * Encuentra las últimas N notificaciones
     */
    public List<Notification> findTop(int limit) {
        return notifications.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Encuentra notificaciones por tipo
     */
    public List<Notification> findByType(String type) {
        return notifications.stream()
                .filter(n -> type.equals(n.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Encuentra notificaciones no leídas
     */
    public List<Notification> findUnread() {
        return notifications.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    /**
     * Encuentra notificaciones desde una fecha específica
     */
    public List<Notification> findSince(LocalDateTime since) {
        return notifications.stream()
                .filter(n -> n.getTimestamp().isAfter(since))
                .collect(Collectors.toList());
    }

    /**
     * Marca una notificación como leída
     */
    public boolean markAsRead(String id) {
        return notifications.stream()
                .filter(n -> id.equals(n.getId()))
                .findFirst()
                .map(n -> {
                    n.setRead(true);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    public void markAllAsRead() {
        notifications.forEach(n -> n.setRead(true));
    }

    /**
     * Elimina notificaciones antiguas (más de X días)
     */
    public int cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        int initialSize = notifications.size();
        
        notifications.removeIf(n -> n.getTimestamp().isBefore(cutoff));
        
        return initialSize - notifications.size();
    }

    /**
     * Obtiene el conteo de notificaciones no leídas
     */
    public long getUnreadCount() {
        return notifications.stream()
                .filter(n -> !n.isRead())
                .count();
    }
}
