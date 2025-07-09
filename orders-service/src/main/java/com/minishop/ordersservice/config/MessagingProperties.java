package com.minishop.ordersservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Clase de configuración para los subjects de mensajería
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
        private Orders orders = new Orders();
        private Notifications notifications = new Notifications();
        private Products products = new Products();
        private Payments payments = new Payments();
        
        public Orders getOrders() {
            return orders;
        }
        
        public void setOrders(Orders orders) {
            this.orders = orders;
        }
        
        public Notifications getNotifications() {
            return notifications;
        }
        
        public void setNotifications(Notifications notifications) {
            this.notifications = notifications;
        }
        
        public Products getProducts() {
            return products;
        }
        
        public void setProducts(Products products) {
            this.products = products;
        }
        
        public Payments getPayments() {
            return payments;
        }
        
        public void setPayments(Payments payments) {
            this.payments = payments;
        }
    }
    
    public static class Orders {
        private String created = "orders.created";
        private String updated = "orders.updated";
        private String cancelled = "orders.cancelled";
        private String statusChanged = "orders.status.changed";
        
        public String getCreated() {
            return created;
        }
        
        public void setCreated(String created) {
            this.created = created;
        }
        
        public String getUpdated() {
            return updated;
        }
        
        public void setUpdated(String updated) {
            this.updated = updated;
        }
        
        public String getCancelled() {
            return cancelled;
        }
        
        public void setCancelled(String cancelled) {
            this.cancelled = cancelled;
        }
        
        public String getStatusChanged() {
            return statusChanged;
        }
        
        public void setStatusChanged(String statusChanged) {
            this.statusChanged = statusChanged;
        }
    }
    
    public static class Notifications {
        private String send = "notifications.send";
        
        public String getSend() {
            return send;
        }
        
        public void setSend(String send) {
            this.send = send;
        }
    }
    
    public static class Products {
        private String checkInventory = "products.inventory.check";
        private String updateInventory = "products.inventory.update";
        
        public String getCheckInventory() {
            return checkInventory;
        }
        
        public void setCheckInventory(String checkInventory) {
            this.checkInventory = checkInventory;
        }
        
        public String getUpdateInventory() {
            return updateInventory;
        }
        
        public void setUpdateInventory(String updateInventory) {
            this.updateInventory = updateInventory;
        }
    }
    
    public static class Payments {
        private String process = "payments.process";
        private String confirmed = "payments.confirmed";
        private String failed = "payments.failed";
        
        public String getProcess() {
            return process;
        }
        
        public void setProcess(String process) {
            this.process = process;
        }
        
        public String getConfirmed() {
            return confirmed;
        }
        
        public void setConfirmed(String confirmed) {
            this.confirmed = confirmed;
        }
        
        public String getFailed() {
            return failed;
        }
        
        public void setFailed(String failed) {
            this.failed = failed;
        }
    }
}
