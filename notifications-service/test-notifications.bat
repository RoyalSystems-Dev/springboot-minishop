@echo off
REM Script de testing para Notifications Service
echo 🧪 Testing Notifications Service...

REM Verificar que el servicio está corriendo
echo 📡 Verificando que el servicio esté activo...
curl -s http://localhost:8083/api/notifications/stats >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Servicio activo en puerto 8083
) else (
    echo ❌ Servicio no está corriendo. Ejecuta: ./mvnw spring-boot:run
    pause
    exit /b 1
)

echo.
echo 🚀 Creando notificaciones de prueba...

echo 📦 Creando notificación ORDER_CREATED...
curl -s -X POST "http://localhost:8083/api/notifications/test" -d "type=ORDER_CREATED&title=Nueva Orden #12345&message=Se ha creado una nueva orden por $150.00&severity=SUCCESS" -H "Content-Type: application/x-www-form-urlencoded" >nul

echo 📉 Creando notificación LOW_STOCK...
curl -s -X POST "http://localhost:8083/api/notifications/test" -d "type=LOW_STOCK&title=Stock Bajo&message=El producto 'Laptop Gaming' tiene solo 2 unidades en stock&severity=ERROR" -H "Content-Type: application/x-www-form-urlencoded" >nul

echo 💰 Creando notificación PAYMENT_CONFIRMED...
curl -s -X POST "http://localhost:8083/api/notifications/test" -d "type=PAYMENT_CONFIRMED&title=Pago Confirmado&message=Pago de $150.00 confirmado para orden #12345&severity=SUCCESS" -H "Content-Type: application/x-www-form-urlencoded" >nul

echo ⚠️ Creando notificación ORDER_CANCELLED...
curl -s -X POST "http://localhost:8083/api/notifications/test" -d "type=ORDER_CANCELLED&title=Orden Cancelada&message=La orden #12346 ha sido cancelada por el cliente&severity=WARNING" -H "Content-Type: application/x-www-form-urlencoded" >nul

echo.
echo 📊 Obteniendo estadísticas...
curl -s http://localhost:8083/api/notifications/stats

echo.
echo 📋 Obteniendo últimas notificaciones...
curl -s "http://localhost:8083/api/notifications/recent?limit=5"

echo.
echo ✅ Testing completado!
echo.
echo 🌐 Abre en tu navegador: http://localhost:8083
echo 📢 Deberías ver las notificaciones de prueba en la interfaz
echo.
echo 🔄 La interfaz se actualizará automáticamente cada 3 segundos
echo 🔊 Puedes habilitar sonidos para nuevas notificaciones
echo 🎯 Usa los filtros para ver solo tipos específicos
echo.
pause
