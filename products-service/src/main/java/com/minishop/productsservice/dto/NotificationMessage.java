package com.minishop.productsservice.dto;

import java.time.LocalDateTime;

/**
 * DTO para mensajes de notificación
 */
public class NotificationMessage {
    private String userId;
    private String message;
    private String type;
    private LocalDateTime timestamp;
    private String channel;
    private Object metadata;
    
    public NotificationMessage() {
        this.timestamp = LocalDateTime.now();
        this.channel = "EMAIL"; // Canal por defecto
    }
    
    public NotificationMessage(String userId, String message, String type) {
        this();
        this.userId = userId;
        this.message = message;
        this.type = type;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "NotificationMessage{" +
                "userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", channel='" + channel + '\'' +
                '}';
    }
}
