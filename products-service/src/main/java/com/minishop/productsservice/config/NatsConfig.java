package com.minishop.productsservice.config;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jakarta.annotation.PreDestroy;
import java.time.Duration;

/**
 * Configuración principal para NATS en products-service
 */
@Configuration
@EnableConfigurationProperties({NatsProperties.class, MessagingProperties.class})
public class NatsConfig {
    
    @Autowired
    private NatsProperties natsProperties;
    
    private Connection natsConnection;
    
    @Bean
    public Connection natsConnection() throws Exception {
        Options options = new Options.Builder()
            .server(natsProperties.getUrl())
            .connectionTimeout(Duration.ofMillis(natsProperties.getConnection().getTimeout()))
            .pingInterval(Duration.ofMillis(natsProperties.getConnection().getPingInterval()))
            .maxReconnects(natsProperties.getConnection().getMaxReconnect())
            .reconnectWait(Duration.ofMillis(natsProperties.getConnection().getReconnectWait()))
            .connectionListener(new ConnectionListener() {
                @Override
                public void connectionEvent(Connection conn, Events type) {
                    switch (type) {
                        case CONNECTED:
                            System.out.println("[PRODUCTS-SERVICE] Connected to NATS server: " + conn.getConnectedUrl());
                            break;
                        case DISCONNECTED:
                            System.out.println("[PRODUCTS-SERVICE] Disconnected from NATS server");
                            break;
                        case RECONNECTED:
                            System.out.println("[PRODUCTS-SERVICE] Reconnected to NATS server: " + conn.getConnectedUrl());
                            break;
                        case CLOSED:
                            System.out.println("[PRODUCTS-SERVICE] Connection to NATS server closed");
                            break;
                        case LAME_DUCK:
                            System.out.println("[PRODUCTS-SERVICE] NATS server entered lame duck mode");
                            break;
                        case DISCOVERED_SERVERS:
                            System.out.println("[PRODUCTS-SERVICE] Discovered new NATS servers");
                            break;
                        case RESUBSCRIBED:
                            System.out.println("[PRODUCTS-SERVICE] Resubscribed to NATS subjects");
                            break;
                        default:
                            System.out.println("[PRODUCTS-SERVICE] Unknown NATS connection event: " + type);
                            break;
                    }
                }
            })
            .build();
        
        this.natsConnection = Nats.connect(options);
        return this.natsConnection;
    }
    
    @PreDestroy
    public void cleanup() {
        if (natsConnection != null && !natsConnection.getStatus().equals(Connection.Status.CLOSED)) {
            try {
                natsConnection.close();
                System.out.println("[PRODUCTS-SERVICE] NATS connection closed successfully");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[PRODUCTS-SERVICE] Error closing NATS connection: " + e.getMessage());
            }
        }
    }
}
