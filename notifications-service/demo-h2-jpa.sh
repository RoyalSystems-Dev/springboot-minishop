#!/bin/bash

echo "🎯 DEMO: H2 + JPA en Notifications Service"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para hacer peticiones
make_request() {
    echo -e "${BLUE}➤ $1${NC}"
    echo -e "${YELLOW}$2${NC}"
    echo ""
    eval $2
    echo ""
    echo "─────────────────────────────"
    echo ""
}

echo -e "${GREEN}🚀 Verificando que el servicio esté corriendo en puerto 8083...${NC}"
echo ""

# Verificar si el servicio está corriendo
if curl -s http://localhost:8083/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Servicio corriendo!${NC}"
else
    echo -e "${YELLOW}⚠️  El servicio no está corriendo. Por favor ejecuta primero:${NC}"
    echo "   ./mvnw spring-boot:run"
    echo ""
    echo -e "${BLUE}💡 Luego ejecuta este script de nuevo${NC}"
    exit 1
fi

echo ""
echo "══════════════════════════════════════════"
echo -e "${GREEN}  🧪 PROBANDO ENDPOINTS JPA + H2  ${NC}"
echo "══════════════════════════════════════════"
echo ""

# 1. Crear algunas notificaciones de prueba
make_request "1. Crear notificación de ERROR" \
'curl -X POST http://localhost:8083/api/jpa/notifications \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"system\",\"title\":\"Error en base de datos\",\"message\":\"Conexión perdida con la BD principal\",\"severity\":\"ERROR\"}"'

make_request "2. Crear notificación de SUCCESS" \
'curl -X POST http://localhost:8083/api/jpa/notifications \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"order\",\"title\":\"Pedido completado\",\"message\":\"El pedido #12345 ha sido procesado exitosamente\",\"severity\":\"SUCCESS\"}"'

make_request "3. Crear notificación de WARNING" \
'curl -X POST http://localhost:8083/api/jpa/notifications \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"inventory\",\"title\":\"Stock bajo\",\"message\":\"Quedan solo 5 unidades del producto XYZ\",\"severity\":\"WARNING\"}"'

make_request "4. Crear notificación de INFO" \
'curl -X POST http://localhost:8083/api/jpa/notifications \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"user\",\"title\":\"Nuevo usuario registrado\",\"message\":\"Usuario juan.perez@email.com se ha registrado\",\"severity\":\"INFO\"}"'

# 2. Obtener todas las notificaciones
make_request "5. Obtener TODAS las notificaciones" \
'curl -s http://localhost:8083/api/jpa/notifications | jq .'

# 3. Filtrar por tipo
make_request "6. Filtrar por tipo: 'order'" \
'curl -s "http://localhost:8083/api/jpa/notifications/type/order" | jq .'

# 4. Filtrar por severidad
make_request "7. Filtrar por severidad: 'ERROR'" \
'curl -s "http://localhost:8083/api/jpa/notifications/severity/ERROR" | jq .'

# 5. Obtener no leídas
make_request "8. Obtener notificaciones NO LEÍDAS" \
'curl -s http://localhost:8083/api/jpa/notifications/unread | jq .'

# 6. Obtener estadísticas
make_request "9. Obtener ESTADÍSTICAS" \
'curl -s http://localhost:8083/api/jpa/notifications/stats | jq .'

# 7. Marcar como leída
echo -e "${BLUE}➤ 10. Marcar notificación ID=1 como LEÍDA${NC}"
echo -e "${YELLOW}curl -X PUT http://localhost:8083/api/jpa/notifications/1/read${NC}"
echo ""
curl -X PUT http://localhost:8083/api/jpa/notifications/1/read
echo ""
echo "─────────────────────────────"
echo ""

# 8. Verificar cambio
make_request "11. Verificar que cambió (ver 'read': true)" \
'curl -s http://localhost:8083/api/jpa/notifications/1 | jq .'

echo ""
echo "══════════════════════════════════════════"
echo -e "${GREEN}  🎉 DEMO COMPLETADO!  ${NC}"
echo "══════════════════════════════════════════"
echo ""
echo -e "${BLUE}🔍 Para ver la base de datos en el navegador:${NC}"
echo "   👉 http://localhost:8083/h2-console"
echo ""
echo -e "${YELLOW}   Configuración H2:${NC}"
echo "   • JDBC URL: jdbc:h2:mem:notificationsdb"
echo "   • User Name: sa"
echo "   • Password: (vacío)"
echo ""
echo -e "${BLUE}📱 Para ver la interfaz web:${NC}"
echo "   👉 http://localhost:8083"
echo ""
echo -e "${GREEN}✨ ¡Todo funcionando con JPA + H2!${NC}"
