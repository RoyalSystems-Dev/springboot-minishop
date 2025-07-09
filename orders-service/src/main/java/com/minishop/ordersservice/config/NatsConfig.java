package com.minishop.ordersservice.config;

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
 * Configuración principal para NATS
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
                            System.out.println("Connected to NATS server: " + conn.getConnectedUrl());
                            break;
                        case DISCONNECTED:
                            System.out.println("Disconnected from NATS server");
                            break;
                        case RECONNECTED:
                            System.out.println("Reconnected to NATS server: " + conn.getConnectedUrl());
                            break;
                        case CLOSED:
                            System.out.println("Connection to NATS server closed");
                            break;
                        case LAME_DUCK:
                            System.out.println("NATS server entered lame duck mode");
                            break;
                        case DISCOVERED_SERVERS:
                            System.out.println("Discovered new NATS servers");
                            break;
                        case RESUBSCRIBED:
                            System.out.println("Resubscribed to NATS subjects");
                            break;
                        default:
                            System.out.println("Unknown NATS connection event: " + type);
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
                System.out.println("NATS connection closed successfully");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Error closing NATS connection: " + e.getMessage());
            }
        }
    }
}
