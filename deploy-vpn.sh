#!/bin/bash

# 🚀 Script de despliegue para Servidor VPN - Mini-Shop
# Configurado para acceso desde VPN (IP: 10.100.88.3)

set -e  # Salir si cualquier comando falla

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Mini-Shop VPN - Build & Deploy Script${NC}"
echo "========================================"
echo ""
echo -e "${YELLOW}Configurado para servidor VPN${NC}"
echo ""

# Variables
PROJECT_NAME="mini-shop"
VERSION=$(date +%Y%m%d-%H%M%S)
VPN_IP="10.100.88.3"

# Verificar Docker Compose (nueva versión integrada o standalone)
check_docker_compose() {
    if docker compose version &> /dev/null; then
        echo "✅ Usando Docker Compose (integrado): $(docker compose version --short)"
        DOCKER_COMPOSE_CMD="docker compose"
    elif command -v docker-compose &> /dev/null; then
        echo "✅ Usando Docker Compose (standalone): $(docker-compose version --short)"
        DOCKER_COMPOSE_CMD="docker-compose"
    else
        echo "❌ Docker Compose no está disponible"
        exit 1
    fi
}

# Función para imprimir mensajes
print_step() {
    echo -e "${GREEN}➤ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar dependencias
check_dependencies() {
    print_step "Verificando dependencias..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado"
        exit 1
    fi
    
    check_docker_compose
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven no está instalado"
        exit 1
    fi
    
    echo "✅ Todas las dependencias están disponibles"
}

# Detectar IP del servidor
detect_server_ip() {
    print_step "Detectando IP del servidor..."
    
    # Detectar IP de la interfaz VPN
    SERVER_IP=$(ip route get 1 | awk '{print $7; exit}' 2>/dev/null || echo "unknown")
    
    if [[ "$SERVER_IP" == "unknown" ]]; then
        # Fallback: detectar IP de interfaces comunes
        SERVER_IP=$(hostname -I | awk '{print $1}' 2>/dev/null || echo "10.100.88.3")
    fi
    
    echo "🌐 IP detectada del servidor: $SERVER_IP"
    echo "🔗 Servicios serán accesibles desde: http://$SERVER_IP"
}

# Limpiar containers anteriores
cleanup() {
    print_step "Limpiando containers anteriores..."
    $DOCKER_COMPOSE_CMD -f docker-compose.vpn.yml down --remove-orphans || true
    docker system prune -f || true
}

# Construir aplicaciones Java
build_services() {
    print_step "Construyendo servicios Java..."
    
    # Build root project
    echo "📦 Building root project..."
    mvn clean install -DskipTests
    
    # Build each service
    for service in orders-service products-service notifications-service; do
        echo "📦 Building $service..."
        cd $service
        mvn clean package -DskipTests
        cd ..
    done
    
    echo "✅ Todos los servicios construidos exitosamente"
}

# Construir imágenes Docker
build_images() {
    print_step "Construyendo imágenes Docker..."
    
    # Usar docker compose para build con perfil VPN
    $DOCKER_COMPOSE_CMD -f docker-compose.vpn.yml build --no-cache
    
    echo "✅ Imágenes Docker construidas exitosamente"
}

# Desplegar servicios
deploy() {
    print_step "Desplegando servicios en VPN..."
    
    # Levantar servicios con configuración VPN
    $DOCKER_COMPOSE_CMD -f docker-compose.vpn.yml up -d
    
    echo "✅ Servicios desplegados exitosamente"
}

# Verificar health checks
verify_deployment() {
    print_step "Verificando despliegue..."
    
    echo "⏳ Esperando que los servicios estén listos..."
    sleep 30
    
    # Verificar cada servicio
    services=("nats-server:8423/healthz" "orders-service:8081/actuator/health" "products-service:8082/actuator/health" "notifications-service:8083/actuator/health")
    
    for service_endpoint in "${services[@]}"; do
        service_name=$(echo $service_endpoint | cut -d':' -f1)
        endpoint="http://localhost:$(echo $service_endpoint | cut -d':' -f2-)"
        
        echo "🔍 Verificando $service_name..."
        
        for i in {1..10}; do
            if curl -s -f "$endpoint" > /dev/null; then
                echo "✅ $service_name está funcionando"
                break
            else
                if [ $i -eq 10 ]; then
                    print_warning "$service_name no responde después de 10 intentos"
                else
                    echo "⏳ Intento $i/10 para $service_name..."
                    sleep 5
                fi
            fi
        done
    done
}

# Verificar firewall
check_firewall() {
    print_step "Verificando configuración de firewall..."
    
    # Verificar si ufw está activo
    if command -v ufw &> /dev/null && ufw status | grep -q "Status: active"; then
        echo "🔥 UFW está activo"
        echo "Verificando reglas para puertos 80, 8081, 8082, 8083, 8422, 8423..."
        
        # Sugerir reglas de firewall
        echo ""
        echo -e "${YELLOW}📋 Reglas de firewall sugeridas:${NC}"
        echo "sudo ufw allow 80"
        echo "sudo ufw allow 8081"
        echo "sudo ufw allow 8082"
        echo "sudo ufw allow 8083"
        echo "sudo ufw allow 8422"
        echo "sudo ufw allow 8423"
        echo ""
        
        read -p "¿Quieres aplicar estas reglas automáticamente? (y/n): " apply_rules
        if [[ "$apply_rules" =~ ^[Yy]$ ]]; then
            sudo ufw allow 80
            sudo ufw allow 8081
            sudo ufw allow 8082
            sudo ufw allow 8083
            sudo ufw allow 8422
            sudo ufw allow 8423
            echo "✅ Reglas de firewall aplicadas"
        fi
    else
        echo "ℹ️  UFW no está activo o no está instalado"
    fi
}

# Mostrar información de los servicios
show_info() {
    print_step "Información de los servicios VPN:"
    echo ""
    echo -e "${GREEN}🌐 Acceso EXTERNO desde tu máquina local:${NC}"
    echo "  • Mini-Shop Portal:      http://$SERVER_IP"
    echo "  • Orders Service:        http://$SERVER_IP/orders-app"
    echo "  • Products Service:      http://$SERVER_IP/products"
    echo "  • Notifications Service: http://$SERVER_IP/notifications-app"
    echo "  • H2 Console:           http://$SERVER_IP/h2-console"
    echo "  • NATS Monitoring:      http://$SERVER_IP:8423"
    echo ""
    echo -e "${BLUE}🔍 Health Checks EXTERNOS:${NC}"
    echo "  • General:              http://$SERVER_IP/health"
    echo "  • Orders:               http://$SERVER_IP:8081/actuator/health"
    echo "  • Products:             http://$SERVER_IP:8082/actuator/health"  
    echo "  • Notifications:        http://$SERVER_IP:8083/actuator/health"
    echo ""
    echo -e "${YELLOW}📊 Comandos útiles:${NC}"
    echo "  • Ver logs:             $DOCKER_COMPOSE_CMD -f docker-compose.vpn.yml logs -f"
    echo "  • Reiniciar servicios:  $DOCKER_COMPOSE_CMD -f docker-compose.vpn.yml restart"
    echo "  • Detener servicios:    $DOCKER_COMPOSE_CMD -f docker-compose.vpn.yml down"
    echo ""
}

# Función principal
main() {
    detect_server_ip
    check_dependencies
    cleanup
    build_services
    build_images
    deploy
    verify_deployment
    check_firewall
    show_info
    
    echo ""
    echo -e "${GREEN}🎉 ¡Mini-Shop desplegado exitosamente en VPN!${NC}"
    echo -e "${BLUE}🔗 Accede desde tu máquina local: http://$SERVER_IP${NC}"
}

# Ejecutar función principal
main
