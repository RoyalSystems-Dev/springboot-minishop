#!/bin/bash

# Script para levantar solo NATS dedicado para Mini-Shop
# Útil cuando quieres desarrollar localmente pero usar NATS en Docker

echo "🚀 Iniciando NATS dedicado para Mini-Shop"
echo "=========================================="
echo ""

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo"
    exit 1
fi

# Verificar si ya existe un contenedor de NATS
if docker ps -a --format "table {{.Names}}" | grep -q "mini-shop-nats"; then
    echo "⚠️  Ya existe un contenedor mini-shop-nats"
    echo "¿Quieres eliminarlo y crear uno nuevo? (y/n)"
    read -p "> " response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        docker rm -f mini-shop-nats-dev 2>/dev/null || true
        docker rm -f mini-shop-nats 2>/dev/null || true
    else
        echo "Abortando..."
        exit 1
    fi
fi

# Crear red si no existe
docker network create mini-shop-dev-net 2>/dev/null || true

# Iniciar NATS
echo "🔄 Iniciando NATS en puerto 8422..."
docker run -d \
    --name mini-shop-nats \
    --network mini-shop-dev-net \
    -p 8422:4222 \
    -p 8423:8222 \
    -p 8424:6222 \
    nats:2.10-alpine \
    --http_port 8222 \
    --name mini-shop-nats-standalone \
    --server_name mini-shop-standalone

# Esperar que NATS esté listo
echo "⏳ Esperando que NATS esté listo..."
sleep 5

# Verificar que NATS está funcionando
if curl -s http://localhost:8423/healthz > /dev/null; then
    echo "✅ NATS está corriendo correctamente!"
    echo ""
    echo "🌐 Accesos disponibles:"
    echo "  • NATS Client:    nats://localhost:8422"
    echo "  • NATS Monitor:   http://localhost:8423"
    echo ""
    echo "🔧 Para desarrollar con este NATS:"
    echo "  export SPRING_PROFILES_ACTIVE=local-with-docker-nats"
    echo "  cd orders-service && ./mvnw spring-boot:run"
    echo "  cd products-service && ./mvnw spring-boot:run"
    echo "  cd notifications-service && ./mvnw spring-boot:run"
    echo ""
    echo "🛑 Para detener NATS:"
    echo "  docker stop mini-shop-nats && docker rm mini-shop-nats"
else
    echo "❌ NATS no está respondiendo"
    echo "Ver logs: docker logs mini-shop-nats"
    exit 1
fi
