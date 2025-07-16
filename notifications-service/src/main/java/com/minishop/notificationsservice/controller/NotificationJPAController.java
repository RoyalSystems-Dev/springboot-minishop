package com.minishop.notificationsservice.controller;

import com.minishop.notificationsservice.model.NotificationJPA;
import com.minishop.notificationsservice.repository.NotificationJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller de ejemplo usando JPA + H2
 * 
 * Este es un ejemplo alternativo a tu NotificationController actual
 * Muestra cómo usar JPA repository en lugar de in-memory
 */
@RestController
@RequestMapping("/notifications-jpa")
@CrossOrigin(origins = "*")
public class NotificationJPAController {

    @Autowired
    private NotificationJPARepository repository;

    /**
     * Obtener todas las notificaciones (con JPA)
     */
    @GetMapping
    public ResponseEntity<List<NotificationJPA>> getAllNotifications() {
        List<NotificationJPA> notifications = repository.findAllOrderByTimestampDesc();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtener las últimas N notificaciones (con JPA)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationJPA>> getRecentNotifications(
            @RequestParam(defaultValue = "50") int limit) {
        List<NotificationJPA> notifications = repository.findTop50ByOrderByTimestampDesc();
        
        // Limitar manualmente si es necesario (o crear método en repository)
        if (notifications.size() > limit) {
            notifications = notifications.subList(0, limit);
        }
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtener notificaciones no leídas (con JPA)
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationJPA>> getUnreadNotifications() {
        List<NotificationJPA> notifications = repository.findByReadFalse();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtener notificaciones por tipo (con JPA)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationJPA>> getNotificationsByType(@PathVariable String type) {
        List<NotificationJPA> notifications = repository.findByType(type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtener notificaciones por severidad (con JPA)
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<NotificationJPA>> getNotificationsBySeverity(@PathVariable String severity) {
        List<NotificationJPA> notifications = repository.findBySeverity(severity);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtener notificaciones desde una fecha específica (con JPA)
     */
    @GetMapping("/since")
    public ResponseEntity<List<NotificationJPA>> getNotificationsSince(
            @RequestParam String timestamp) {
        try {
            LocalDateTime since = LocalDateTime.parse(timestamp);
            List<NotificationJPA> notifications = repository.findRecentNotifications(since);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Marcar una notificación como leída (con JPA)
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        Optional<NotificationJPA> notificationOpt = repository.findById(id);
        
        if (notificationOpt.isPresent()) {
            NotificationJPA notification = notificationOpt.get();
            notification.setRead(true);
            repository.save(notification);  // JPA guarda automáticamente
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification marked as read",
                    "id", id
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Notification not found",
                    "id", id
            ));
        }
    }

    /**
     * Marcar todas las notificaciones como leídas (con JPA)
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        int updatedCount = repository.markAllAsRead();
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All notifications marked as read",
                "updated", updatedCount
        ));
    }

    /**
     * Obtener estadísticas de notificaciones (con JPA)
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        long totalCount = repository.count();
        long unreadCount = repository.countByReadFalse();
        
        // Estadísticas por tipo
        List<Object[]> typeStats = repository.getNotificationCountByType();
        Map<String, Long> typeCount = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeCount.put((String) stat[0], ((Number) stat[1]).longValue());
        }
        
        // Estadísticas por severidad
        List<Object[]> severityStats = repository.getNotificationCountBySeverity();
        Map<String, Long> severityCount = new HashMap<>();
        for (Object[] stat : severityStats) {
            severityCount.put((String) stat[0], ((Number) stat[1]).longValue());
        }

        return ResponseEntity.ok(Map.of(
                "total", totalCount,
                "unread", unreadCount,
                "byType", typeCount,
                "bySeverity", severityCount,
                "lastUpdate", LocalDateTime.now()
        ));
    }

    /**
     * Crear notificación de prueba (con JPA)
     */
    @PostMapping("/test")
    public ResponseEntity<NotificationJPA> createTestNotification(
            @RequestParam(defaultValue = "TEST") String type,
            @RequestParam(defaultValue = "Test Notification") String title,
            @RequestParam(defaultValue = "This is a test notification") String message,
            @RequestParam(defaultValue = "INFO") String severity) {
        
        NotificationJPA notification = new NotificationJPA(type, title, message, severity);
        NotificationJPA saved = repository.save(notification);  // JPA guarda automáticamente
        
        return ResponseEntity.ok(saved);
    }

    /**
     * Eliminar notificación (con JPA)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification deleted",
                    "id", id
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Notification not found",
                    "id", id
            ));
        }
    }

    /**
     * Limpiar notificaciones antiguas (con JPA)
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldNotifications(
            @RequestParam(defaultValue = "7") int daysToKeep) {
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        repository.deleteOldNotifications(cutoffDate);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Old notifications cleaned up",
                "cutoffDate", cutoffDate
        ));
    }
}
