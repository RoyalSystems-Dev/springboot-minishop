package com.minishop.notificationsservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Versión JPA de la entidad Notification
 * Esta es una alternativa a tu modelo actual usando JPA + H2
 */
@Entity
@Table(name = "notifications")
public class NotificationJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "notification_type", nullable = false, length = 50)
    private String type;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false, length = 20)
    private String severity;
    
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    // Constructores
    public NotificationJPA() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificationJPA(String type, String title, String message, String severity) {
        this();
        this.type = type;
        this.title = title;
        this.message = message;
        this.severity = severity;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "NotificationJPA{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", severity='" + severity + '\'' +
                ", read=" + read +
                ", timestamp=" + timestamp +
                '}';
    }
}
