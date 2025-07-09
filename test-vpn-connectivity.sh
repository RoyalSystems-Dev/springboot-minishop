#!/bin/bash

# Script para verificar conectividad VPN a Mini-Shop
# Ejecutar desde tu máquina local

VPN_SERVER_IP="10.100.88.3"

echo "🔍 Verificando conectividad con Mini-Shop en VPN"
echo "==============================================="
echo ""
echo "🌐 Servidor VPN: $VPN_SERVER_IP"
echo ""

# Función para probar conectividad
test_endpoint() {
    local url=$1
    local name=$2
    
    echo -n "🔗 Probando $name... "
    
    if curl -s --connect-timeout 5 --max-time 10 "$url" > /dev/null; then
        echo "✅ OK"
        return 0
    else
        echo "❌ FALLO"
        return 1
    fi
}

# Probar conectividad básica
echo "📡 Conectividad básica:"
if ping -c 1 -W 3 $VPN_SERVER_IP > /dev/null 2>&1; then
    echo "✅ Ping al servidor: OK"
else
    echo "❌ Ping al servidor: FALLO"
    echo ""
    echo "🚨 No hay conectividad básica con el servidor VPN"
    echo "   Verifica que estés conectado a la VPN"
    exit 1
fi

echo ""
echo "🌐 Servicios Web:"

# Probar cada servicio
test_endpoint "http://$VPN_SERVER_IP/" "Portal Principal"
test_endpoint "http://$VPN_SERVER_IP/health" "Health Check General"
test_endpoint "http://$VPN_SERVER_IP:8081/actuator/health" "Orders Service"
test_endpoint "http://$VPN_SERVER_IP:8082/actuator/health" "Products Service"
test_endpoint "http://$VPN_SERVER_IP:8083/actuator/health" "Notifications Service"
test_endpoint "http://$VPN_SERVER_IP:8423/healthz" "NATS Monitoring"

echo ""
echo "🔍 Puertos específicos:"

# Verificar puertos con netcat si está disponible
if command -v nc >/dev/null 2>&1; then
    ports=(80 8081 8082 8083 8422 8423)
    for port in "${ports[@]}"; do
        echo -n "🔌 Puerto $port... "
        if nc -z -w3 $VPN_SERVER_IP $port 2>/dev/null; then
            echo "✅ ABIERTO"
        else
            echo "❌ CERRADO"
        fi
    done
else
    echo "ℹ️  netcat no disponible, omitiendo verificación de puertos"
fi

echo ""
echo "📋 URLs de acceso directo:"
echo "  • Portal:        http://$VPN_SERVER_IP"
echo "  • Orders:        http://$VPN_SERVER_IP/orders-app"
echo "  • Products:      http://$VPN_SERVER_IP/products"
echo "  • Notifications: http://$VPN_SERVER_IP/notifications-app"
echo "  • H2 Console:    http://$VPN_SERVER_IP/h2-console"
echo "  • NATS Monitor:  http://$VPN_SERVER_IP:8423"
echo ""
echo "💡 Si algo falla, verifica:"
echo "  1. Conectividad VPN"
echo "  2. Firewall del servidor (puertos 80, 808X, 842X)"
echo "  3. Que los servicios estén corriendo en el servidor"
