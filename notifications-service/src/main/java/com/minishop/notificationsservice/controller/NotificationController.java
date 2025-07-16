package com.minishop.notificationsservice.controller;

import com.minishop.notificationsservice.model.Notification;
import com.minishop.notificationsservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el manejo de notificaciones
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Obtiene todas las notificaciones
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene las últimas N notificaciones
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Notification>> getRecentNotifications(
            @RequestParam(defaultValue = "50") int limit) {
        List<Notification> notifications = notificationRepository.findTop(limit);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones no leídas
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        List<Notification> notifications = notificationRepository.findUnread();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones por tipo
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable String type) {
        List<Notification> notifications = notificationRepository.findByType(type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones desde una fecha específica (para polling)
     */
    @GetMapping("/since")
    public ResponseEntity<List<Notification>> getNotificationsSince(
            @RequestParam String timestamp) {
        try {
            LocalDateTime since = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            List<Notification> notifications = notificationRepository.findSince(since);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Marca una notificación como leída
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable String id) {
        boolean success = notificationRepository.markAsRead(id);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "Notification marked as read" : "Notification not found"
        ));
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        notificationRepository.markAllAsRead();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All notifications marked as read"
        ));
    }

    /**
     * Obtiene estadísticas de notificaciones
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        List<Notification> allNotifications = notificationRepository.findAll();
        long unreadCount = notificationRepository.getUnreadCount();
        
        Map<String, Long> typeCount = allNotifications.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Notification::getType,
                        java.util.stream.Collectors.counting()
                ));

        Map<String, Long> severityCount = allNotifications.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Notification::getSeverity,
                        java.util.stream.Collectors.counting()
                ));

        return ResponseEntity.ok(Map.of(
                "total", allNotifications.size(),
                "unread", unreadCount,
                "byType", typeCount,
                "bySeverity", severityCount,
                "lastUpdate", LocalDateTime.now()
        ));
    }

    /**
     * Endpoint para crear notificaciones de prueba
     */
    @PostMapping("/test")
    public ResponseEntity<Notification> createTestNotification(
            @RequestParam(defaultValue = "TEST") String type,
            @RequestParam(defaultValue = "Test Notification") String title,
            @RequestParam(defaultValue = "This is a test notification") String message,
            @RequestParam(defaultValue = "INFO") String severity) {
        
        Notification notification = new Notification(type, title, message, severity);
        Notification saved = notificationRepository.save(notification);
        
        return ResponseEntity.ok(saved);
    }
}
