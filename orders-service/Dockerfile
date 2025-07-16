# Dockerfile para orders-service
FROM openjdk:17-jdk-slim

# Metadatos
LABEL maintainer="mini-shop"
LABEL service="orders-service"
LABEL version="1.0"

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear directorio de trabajo
WORKDIR /app

# Crear usuario no-root para seguridad
RUN groupadd -r spring && useradd -r -g spring spring

# Copiar el JAR construido
COPY target/orders-service-*.jar app.jar

# Copiar archivos estáticos (public)
COPY public/ /app/public/

# Cambiar propietario de los archivos
RUN chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring

# Puerto que expone el servicio
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="docker"

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
