package com.minishop.productsservice.dto;

import java.time.LocalDateTime;

/**
 * DTO para eventos de productos
 */
public class ProductEvent {
    private String productId;
    private String name;
    private Double price;
    private String action;
    private LocalDateTime timestamp;
    private Object data;
    
    public ProductEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ProductEvent(String productId, String name, Double price, String action) {
        this();
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.action = action;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "ProductEvent{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
