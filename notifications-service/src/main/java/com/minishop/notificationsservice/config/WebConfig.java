package com.minishop.notificationsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para servir archivos estáticos desde el directorio public
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde public/ para hot-reload en desarrollo
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/public/")
                .addResourceLocations("classpath:/static/", "classpath:/public/")
                .setCachePeriod(0); // Sin cache para desarrollo
    }
}
