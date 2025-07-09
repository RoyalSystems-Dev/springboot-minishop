package com.minishop.ordersservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Clase de configuración para las propiedades de NATS
 */
@Component
@ConfigurationProperties(prefix = "nats")
public class NatsProperties {
    
    private String url = "nats://localhost:4222";
    private Connection connection = new Connection();
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();
    private Cluster cluster = new Cluster();
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public Consumer getConsumer() {
        return consumer;
    }
    
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
    
    public Producer getProducer() {
        return producer;
    }
    
    public void setProducer(Producer producer) {
        this.producer = producer;
    }
    
    public Cluster getCluster() {
        return cluster;
    }
    
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public static class Connection {
        private int timeout = 2000;
        private int maxReconnect = 60;
        private int reconnectWait = 2000;
        private long pingInterval = 120000;
        private long cleanupInterval = 30000;
        
        public int getTimeout() {
            return timeout;
        }
        
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
        
        public int getMaxReconnect() {
            return maxReconnect;
        }
        
        public void setMaxReconnect(int maxReconnect) {
            this.maxReconnect = maxReconnect;
        }
        
        public int getReconnectWait() {
            return reconnectWait;
        }
        
        public void setReconnectWait(int reconnectWait) {
            this.reconnectWait = reconnectWait;
        }
        
        public long getPingInterval() {
            return pingInterval;
        }
        
        public void setPingInterval(long pingInterval) {
            this.pingInterval = pingInterval;
        }
        
        public long getCleanupInterval() {
            return cleanupInterval;
        }
        
        public void setCleanupInterval(long cleanupInterval) {
            this.cleanupInterval = cleanupInterval;
        }
    }
    
    public static class Consumer {
        private int maxDeliver = 3;
        private long ackWait = 30000;
        
        public int getMaxDeliver() {
            return maxDeliver;
        }
        
        public void setMaxDeliver(int maxDeliver) {
            this.maxDeliver = maxDeliver;
        }
        
        public long getAckWait() {
            return ackWait;
        }
        
        public void setAckWait(long ackWait) {
            this.ackWait = ackWait;
        }
    }
    
    public static class Producer {
        private int maxPending = 1000;
        private int timeout = 2000;
        
        public int getMaxPending() {
            return maxPending;
        }
        
        public void setMaxPending(int maxPending) {
            this.maxPending = maxPending;
        }
        
        public int getTimeout() {
            return timeout;
        }
        
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }
    
    public static class Cluster {
        private boolean enabled = false;
        private String[] servers;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String[] getServers() {
            return servers;
        }
        
        public void setServers(String[] servers) {
            this.servers = servers;
        }
    }
}
