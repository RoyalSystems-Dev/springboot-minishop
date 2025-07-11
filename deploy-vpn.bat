@echo off
REM 🚀 Script de despliegue para Servidor VPN - Mini-Shop (Windows)
REM Configurado para puerto 8088 (evita conflictos con Nginx existente)

echo 🚀 Mini-Shop VPN - Build ^& Deploy Script (Puerto 8088)
echo ========================================================
echo.
echo 🔧 Configurado para servidor VPN (Puerto 8088)
echo.

REM Variables
set PROJECT_NAME=mini-shop
set TIMESTAMP=%date:~10,4%%date:~4,2%%date:~7,2%-%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

echo ¿Qué operación deseas realizar?
echo 1. 🔨 Build completo VPN (limpiar + construir + desplegar puerto 8088)
echo 2. 🚀 Solo desplegar VPN (usar imágenes existentes)
echo 3. 🧹 Solo limpiar
echo 4. 📦 Solo construir (sin desplegar)
echo 5. 🔍 Ver estado de servicios VPN
echo 6. 📋 Ver logs VPN
echo.
set /p choice="Selecciona una opción (1-6): "

if "%choice%"=="1" goto build_complete_vpn
if "%choice%"=="2" goto deploy_vpn_only
if "%choice%"=="3" goto cleanup_vpn
if "%choice%"=="4" goto build_only
if "%choice%"=="5" goto show_status_vpn
if "%choice%"=="6" goto show_logs_vpn
goto invalid_option

:build_complete_vpn
echo.
echo ➤ Verificando dependencias...
call :check_dependencies
if errorlevel 1 exit /b 1

echo ➤ Limpiando containers anteriores...
call :cleanup_vpn

echo ➤ Construyendo servicios Java...
call :build_services
if errorlevel 1 exit /b 1

echo ➤ Construyendo imágenes Docker...
call :build_images_vpn
if errorlevel 1 exit /b 1

echo ➤ Desplegando servicios VPN (Puerto 8088)...
call :deploy_vpn
if errorlevel 1 exit /b 1

echo ➤ Verificando despliegue...
call :verify_deployment_vpn

call :show_info_vpn
goto end

:deploy_vpn_only
echo.
echo ➤ Verificando dependencias...
call :check_dependencies
if errorlevel 1 exit /b 1

echo ➤ Desplegando servicios VPN (Puerto 8088)...
call :deploy_vpn
if errorlevel 1 exit /b 1

echo ➤ Verificando despliegue...
call :verify_deployment_vpn

call :show_info_vpn
goto end

:cleanup_vpn
echo.
echo ➤ Limpiando containers VPN anteriores...
call :cleanup_vpn
echo ✅ Limpieza VPN completada
goto end

:build_only
echo.
echo ➤ Verificando dependencias...
call :check_dependencies
if errorlevel 1 exit /b 1

echo ➤ Construyendo servicios Java...
call :build_services
if errorlevel 1 exit /b 1

echo ➤ Construyendo imágenes Docker VPN...
call :build_images_vpn
if errorlevel 1 exit /b 1

echo ✅ Construcción VPN completada
goto end

:show_status_vpn
docker-compose -f docker-compose.vpn.yml ps
echo.
docker-compose -f docker-compose.vpn.yml logs --tail=50
goto end

:show_logs_vpn
echo.
echo ¿De qué servicio quieres ver los logs?
echo 1. Todos
echo 2. Orders
echo 3. Products
echo 4. Notifications
echo 5. NATS
echo 6. Nginx
set /p log_choice="Selecciona (1-6): "

if "%log_choice%"=="1" docker-compose -f docker-compose.vpn.yml logs -f
if "%log_choice%"=="2" docker-compose -f docker-compose.vpn.yml logs -f orders-service
if "%log_choice%"=="3" docker-compose -f docker-compose.vpn.yml logs -f products-service
if "%log_choice%"=="4" docker-compose -f docker-compose.vpn.yml logs -f notifications-service
if "%log_choice%"=="5" docker-compose -f docker-compose.vpn.yml logs -f nats-server
if "%log_choice%"=="6" docker-compose -f docker-compose.vpn.yml logs -f nginx
goto end

:invalid_option
echo Opción inválida
exit /b 1

REM ========================================
REM FUNCIONES VPN
REM ========================================

:check_dependencies
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker no está instalado
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose no está instalado
    exit /b 1
)

mvn --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven no está instalado
    exit /b 1
)

echo ✅ Todas las dependencias están disponibles
exit /b 0

:cleanup_vpn
docker-compose -f docker-compose.vpn.yml down --remove-orphans 2>nul
docker system prune -f 2>nul
exit /b 0

:build_services
echo 📦 Building root project...
mvn clean install -DskipTests
if errorlevel 1 exit /b 1

echo 📦 Building orders-service...
cd orders-service
mvn clean package -DskipTests
if errorlevel 1 exit /b 1
cd ..

echo 📦 Building products-service...
cd products-service
mvn clean package -DskipTests
if errorlevel 1 exit /b 1
cd ..

echo 📦 Building notifications-service...
cd notifications-service
mvn clean package -DskipTests
if errorlevel 1 exit /b 1
cd ..

echo ✅ Todos los servicios construidos exitosamente
exit /b 0

:build_images_vpn
docker-compose -f docker-compose.vpn.yml build --no-cache
if errorlevel 1 exit /b 1
echo ✅ Imágenes Docker VPN construidas exitosamente
exit /b 0

:deploy_vpn
docker-compose -f docker-compose.vpn.yml up -d
if errorlevel 1 exit /b 1
echo ✅ Servicios VPN desplegados exitosamente
exit /b 0

:verify_deployment_vpn
echo Esperando que los servicios estén listos...
timeout /t 30 /nobreak >nul

echo 🔍 Verificando servicios VPN...
REM Aquí podrías agregar checks específicos con curl si está disponible
echo ⏳ Los servicios están iniciando... Verifica manualmente en el browser
echo 🌐 Acceso: http://localhost:8088
exit /b 0

:show_info_vpn
echo.
echo ➤ Información de los servicios VPN (Puerto 8088):
echo.
echo 🌐 Acceso a los servicios:
echo   • Mini-Shop Portal:      http://localhost:8088
echo   • Orders Service:        http://localhost:8088/orders-app
echo   • Products Service:      http://localhost:8088/products
echo   • Notifications Service: http://localhost:8088/notifications-app
echo   • H2 Console:           http://localhost:8088/h2-console
echo   • NATS Monitoring:      http://localhost:8423
echo.
echo 🔍 Health Checks:
echo   • General:              http://localhost:8088/health
echo   • Orders:               http://localhost:8088/health/orders
echo   • Products:             http://localhost:8088/health/products
echo   • Notifications:        http://localhost:8088/health/notifications
echo.
echo 📊 Logs:
echo   • Ver todos los logs:   docker-compose -f docker-compose.vpn.yml logs -f
echo   • Ver logs específicos: docker-compose -f docker-compose.vpn.yml logs -f [service-name]
echo.
echo 🔧 Configuración especial:
echo   • Puerto 8088 usado para evitar conflictos con Nginx existente
echo   • Para acceso externo, reemplaza 'localhost' con IP del servidor
echo.
exit /b 0

:end
echo.
echo 🎉 Proceso VPN completado!
echo 🔗 Accede a: http://localhost:8088
pause
