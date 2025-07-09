package com.minishop.productsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Clase de configuración para los subjects de mensajería del products-service
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
        private Products products = new Products();
        private Orders orders = new Orders();
        private Notifications notifications = new Notifications();
        
        public Products getProducts() {
            return products;
        }
        
        public void setProducts(Products products) {
            this.products = products;
        }
        
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
    }
    
    public static class Products {
        private String created = "products.created";
        private String updated = "products.updated";
        private String deleted = "products.deleted";
        private String inventoryCheck = "products.inventory.check";
        private String inventoryUpdate = "products.inventory.update";
        private String lowStock = "products.stock.low";
        
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
        
        public String getDeleted() {
            return deleted;
        }
        
        public void setDeleted(String deleted) {
            this.deleted = deleted;
        }
        
        public String getInventoryCheck() {
            return inventoryCheck;
        }
        
        public void setInventoryCheck(String inventoryCheck) {
            this.inventoryCheck = inventoryCheck;
        }
        
        public String getInventoryUpdate() {
            return inventoryUpdate;
        }
        
        public void setInventoryUpdate(String inventoryUpdate) {
            this.inventoryUpdate = inventoryUpdate;
        }
        
        public String getLowStock() {
            return lowStock;
        }
        
        public void setLowStock(String lowStock) {
            this.lowStock = lowStock;
        }
    }
    
    public static class Orders {
        private String created = "orders.created";
        private String updated = "orders.updated";
        private String cancelled = "orders.cancelled";
        
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
}
