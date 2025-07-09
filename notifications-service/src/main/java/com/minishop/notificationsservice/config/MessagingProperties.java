package com.minishop.notificationsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración de subjects para notifications-service
 */
@Component
@ConfigurationProperties(prefix = "messaging")
public class MessagingProperties {
    
    private Subjects subjects = new Subjects();
    
    public Subjects getSubjects() {
        return subjects;
    }
    
    public void setSubjects(Subjects subjects) {
        this.subjects = subjects;
    }
    
    public static class Subjects {
        private Notifications notifications = new Notifications();
        private Orders orders = new Orders();
        private Products products = new Products();
        private Payments payments = new Payments();
        
        // Getters y setters
        public Notifications getNotifications() { return notifications; }
        public void setNotifications(Notifications notifications) { this.notifications = notifications; }
        public Orders getOrders() { return orders; }
        public void setOrders(Orders orders) { this.orders = orders; }
        public Products getProducts() { return products; }
        public void setProducts(Products products) { this.products = products; }
        public Payments getPayments() { return payments; }
        public void setPayments(Payments payments) { this.payments = payments; }
    }
    
    public static class Notifications {
        private String send = "notifications.send";
        private String email = "notifications.email";
        private String sms = "notifications.sms";
        private String push = "notifications.push";
        
        // Getters y setters
        public String getSend() { return send; }
        public void setSend(String send) { this.send = send; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSms() { return sms; }
        public void setSms(String sms) { this.sms = sms; }
        public String getPush() { return push; }
        public void setPush(String push) { this.push = push; }
    }
    
    public static class Orders {
        private String created = "orders.created";
        private String updated = "orders.updated";
        private String cancelled = "orders.cancelled";
        private String statusChanged = "orders.status.changed";
        
        // Getters y setters
        public String getCreated() { return created; }
        public void setCreated(String created) { this.created = created; }
        public String getUpdated() { return updated; }
        public void setUpdated(String updated) { this.updated = updated; }
        public String getCancelled() { return cancelled; }
        public void setCancelled(String cancelled) { this.cancelled = cancelled; }
        public String getStatusChanged() { return statusChanged; }
        public void setStatusChanged(String statusChanged) { this.statusChanged = statusChanged; }
    }
    
    public static class Products {
        private String lowStock = "products.stock.low";
        
        public String getLowStock() { return lowStock; }
        public void setLowStock(String lowStock) { this.lowStock = lowStock; }
    }
    
    public static class Payments {
        private String confirmed = "payments.confirmed";
        private String failed = "payments.failed";
        
        public String getConfirmed() { return confirmed; }
        public void setConfirmed(String confirmed) { this.confirmed = confirmed; }
        public String getFailed() { return failed; }
        public void setFailed(String failed) { this.failed = failed; }
    }
}
