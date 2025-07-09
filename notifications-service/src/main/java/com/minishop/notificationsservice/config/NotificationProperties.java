package com.minishop.notificationsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración para los canales de notificación
 */
@Component
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {
    
    private Channels channels = new Channels();
    
    public Channels getChannels() {
        return channels;
    }
    
    public void setChannels(Channels channels) {
        this.channels = channels;
    }
    
    public static class Channels {
        private Email email = new Email();
        private Sms sms = new Sms();
        private Push push = new Push();
        
        public Email getEmail() { return email; }
        public void setEmail(Email email) { this.email = email; }
        public Sms getSms() { return sms; }
        public void setSms(Sms sms) { this.sms = sms; }
        public Push getPush() { return push; }
        public void setPush(Push push) { this.push = push; }
    }
    
    public static class Email {
        private boolean enabled = true;
        private String smtpHost = "localhost";
        private int smtpPort = 587;
        private String username = "noreply@minishop.com";
        private String password = "password";
        
        // Getters y setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getSmtpHost() { return smtpHost; }
        public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }
        public int getSmtpPort() { return smtpPort; }
        public void setSmtpPort(int smtpPort) { this.smtpPort = smtpPort; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class Sms {
        private boolean enabled = false;
        private String provider = "twilio";
        private String apiKey = "your-api-key";
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }
    
    public static class Push {
        private boolean enabled = false;
        private String firebaseKey = "your-firebase-key";
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getFirebaseKey() { return firebaseKey; }
        public void setFirebaseKey(String firebaseKey) { this.firebaseKey = firebaseKey; }
    }
}
