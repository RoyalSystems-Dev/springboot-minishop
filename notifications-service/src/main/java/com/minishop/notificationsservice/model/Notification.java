package com.minishop.notificationsservice.model;

import java.time.LocalDateTime;

/**
 * Modelo de notificación para la interfaz
 */
public class Notification {
    private String id;
    private String type;
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private String severity; // INFO, WARNING, ERROR, SUCCESS
    private boolean read;

    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.read = false;
        this.id = generateId();
    }

    public Notification(String type, String title, String message, String severity) {
        this();
        this.type = type;
        this.title = title;
        this.message = message;
        this.severity = severity;
    }

    private String generateId() {
        return System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
