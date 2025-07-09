package com.minishop.productsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar para servir archivos estáticos desde public/ (desarrollo sin compilar)
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/public/")
                .addResourceLocations("classpath:/static/", "classpath:/public/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir la raíz a index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
