@echo off
echo 🎯 DEMO: H2 + JPA en Notifications Service
echo ==========================================
echo.

:: Verificar si el servicio está corriendo
echo 🚀 Verificando que el servicio esté corriendo en puerto 8083...
echo.

curl -s http://localhost:8083/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  El servicio no está corriendo. Por favor ejecuta primero:
    echo    ./mvnw spring-boot:run
    echo.
    echo 💡 Luego ejecuta este script de nuevo
    pause
    exit /b 1
)

echo ✅ Servicio corriendo!
echo.
echo ══════════════════════════════════════════
echo   🧪 PROBANDO ENDPOINTS JPA + H2  
echo ══════════════════════════════════════════
echo.

echo ➤ 1. Crear notificación de ERROR
curl -X POST http://localhost:8083/api/jpa/notifications -H "Content-Type: application/json" -d "{\"type\":\"system\",\"title\":\"Error en base de datos\",\"message\":\"Conexión perdida con la BD principal\",\"severity\":\"ERROR\"}"
echo.
echo ─────────────────────────────
echo.

echo ➤ 2. Crear notificación de SUCCESS
curl -X POST http://localhost:8083/api/jpa/notifications -H "Content-Type: application/json" -d "{\"type\":\"order\",\"title\":\"Pedido completado\",\"message\":\"El pedido #12345 ha sido procesado exitosamente\",\"severity\":\"SUCCESS\"}"
echo.
echo ─────────────────────────────
echo.

echo ➤ 3. Crear notificación de WARNING  
curl -X POST http://localhost:8083/api/jpa/notifications -H "Content-Type: application/json" -d "{\"type\":\"inventory\",\"title\":\"Stock bajo\",\"message\":\"Quedan solo 5 unidades del producto XYZ\",\"severity\":\"WARNING\"}"
echo.
echo ─────────────────────────────
echo.

echo ➤ 4. Crear notificación de INFO
curl -X POST http://localhost:8083/api/jpa/notifications -H "Content-Type: application/json" -d "{\"type\":\"user\",\"title\":\"Nuevo usuario registrado\",\"message\":\"Usuario juan.perez@email.com se ha registrado\",\"severity\":\"INFO\"}"
echo.
echo ─────────────────────────────
echo.

echo ➤ 5. Obtener TODAS las notificaciones
curl -s http://localhost:8083/api/jpa/notifications
echo.
echo ─────────────────────────────
echo.

echo ➤ 6. Filtrar por tipo: 'order'
curl -s "http://localhost:8083/api/jpa/notifications/type/order"
echo.
echo ─────────────────────────────
echo.

echo ➤ 7. Filtrar por severidad: 'ERROR'
curl -s "http://localhost:8083/api/jpa/notifications/severity/ERROR"
echo.
echo ─────────────────────────────
echo.

echo ➤ 8. Obtener notificaciones NO LEÍDAS
curl -s http://localhost:8083/api/jpa/notifications/unread
echo.
echo ─────────────────────────────
echo.

echo ➤ 9. Obtener ESTADÍSTICAS
curl -s http://localhost:8083/api/jpa/notifications/stats
echo.
echo ─────────────────────────────
echo.

echo ➤ 10. Marcar notificación ID=1 como LEÍDA
curl -X PUT http://localhost:8083/api/jpa/notifications/1/read
echo.
echo ─────────────────────────────
echo.

echo ➤ 11. Verificar que cambió (ver 'read': true)
curl -s http://localhost:8083/api/jpa/notifications/1
echo.
echo ─────────────────────────────
echo.

echo.
echo ══════════════════════════════════════════
echo   🎉 DEMO COMPLETADO!  
echo ══════════════════════════════════════════
echo.
echo 🔍 Para ver la base de datos en el navegador:
echo    👉 http://localhost:8083/h2-console
echo.
echo    Configuración H2:
echo    • JDBC URL: jdbc:h2:mem:notificationsdb
echo    • User Name: sa  
echo    • Password: (vacío)
echo.
echo 📱 Para ver la interfaz web:
echo    👉 http://localhost:8083
echo.
echo ✨ ¡Todo funcionando con JPA + H2!
echo.
pause
