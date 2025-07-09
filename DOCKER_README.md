# 🐳 Mini-Shop - Guía de Docker

## 📋 **Resumen de la Arquitectura**

```
🌐 Nginx (Puerto 80)
    ↓
📦 Orders Service (Puerto 8081)
📦 Products Service (Puerto 8082)  
📦 Notifications Service (Puerto 8083)
    ↓
📡 NATS Server Dedicado (Puerto 8422)
```

---

## 🚀 **Despliegue Rápido**

### **Opción 1: Script Automático (Recomendado)**

#### **Linux/Mac:**
```bash
chmod +x deploy.sh
./deploy.sh
```

#### **Windows:**
```cmd
deploy.bat
```

### **Opción 2: Comandos Manuales**

```bash
# 1. Construir servicios Java
mvn clean install -DskipTests

# 2. Construir imágenes Docker
docker-compose build

# 3. Desplegar servicios
docker-compose up -d

# 4. Verificar estado
docker-compose ps
```

### **Opción 3: Makefile (Linux/Mac)**

```bash
# Ver comandos disponibles
make help

# Build + Deploy completo
make up

# Solo infraestructura para desarrollo
make dev-up

# Ver logs
make logs

# Limpiar todo
make clean
```

---

## 🔧 **Configuraciones de Entorno**

### **Producción (`docker-compose.yml`)**
- ✅ Todos los servicios en contenedores
- ✅ Nginx como reverse proxy
- ✅ Health checks configurados
- ✅ Volúmenes persistentes
- ✅ Red interna aislada

### **Desarrollo (`docker-compose.dev.yml`)**
- ✅ Solo infraestructura (NATS, PostgreSQL, Redis)
- ✅ Servicios Spring Boot ejecutados localmente
- ✅ Hot-reload habilitado
- ✅ Herramientas de desarrollo (Adminer)

---

## 🌐 **Puertos y Acceso**

| **Servicio** | **Puerto** | **URL** | **Descripción** |
|--------------|------------|---------|-----------------|
| **Nginx** | 80 | http://localhost | Portal principal |
| **Orders** | 8081 | http://localhost/orders-app | Interfaz de órdenes |
| **Products** | 8082 | http://localhost/products | API de productos |
| **Notifications** | 8083 | http://localhost/notifications-app | Interfaz de notificaciones |
| **H2 Console** | - | http://localhost/h2-console | Base de datos H2 |
| **NATS Monitor** | 8423 | http://localhost:8423 | Monitoreo NATS (dedicado) |
| **NATS Client** | 8422 | nats://localhost:8422 | Cliente NATS (dedicado) |

---

## 📊 **Comandos Útiles**

### **Estado y Monitoreo**
```bash
# Ver servicios corriendo
docker-compose ps

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f orders-service

# Ver uso de recursos
docker stats

# Health checks
curl http://localhost/health
```

### **Gestión de Servicios**
```bash
# Reiniciar un servicio
docker-compose restart orders-service

# Reiniciar todos
docker-compose restart

# Detener todos
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### **Desarrollo y Debug**
```bash
# Entrar al contenedor
docker-compose exec orders-service /bin/sh

# Ver variables de entorno
docker-compose exec orders-service env

# Reconstruir un servicio
docker-compose build orders-service

# Reconstruir sin cache
docker-compose build --no-cache
```

---

## 🔍 **Troubleshooting**

### **Problemas Comunes**

#### **1. Servicios no inician**
```bash
# Ver logs detallados
docker-compose logs orders-service

# Verificar health checks
docker-compose ps

# Reiniciar servicio problemático
docker-compose restart orders-service
```

#### **2. Puerto ocupado**
```bash
# Ver qué proceso usa el puerto
netstat -tulpn | grep :8081

# Cambiar puerto en docker-compose.yml
ports:
  - "8181:8081"  # Puerto externo diferente
```

#### **3. Problema de memoria**
```bash
# Verificar uso de memoria
docker stats

# Ajustar memoria en docker-compose.yml
environment:
  - JAVA_OPTS=-Xmx256m -Xms128m
```

#### **4. Conectividad entre servicios**
```bash
# Verificar red
docker network ls
docker network inspect mini-shop-net

# Probar conectividad
docker-compose exec orders-service ping nats-server
```

### **Logs y Debug**

#### **Ver logs específicos:**
```bash
# Solo errores
docker-compose logs orders-service | grep ERROR

# Últimas 100 líneas
docker-compose logs --tail=100 orders-service

# Logs en tiempo real con timestamp
docker-compose logs -f -t orders-service
```

#### **Debug de NATS:**
```bash
# Monitoring web de NATS (instancia dedicada)
curl http://localhost:8423/connz

# Ver subjects activos
curl http://localhost:8423/subsz
```

---

## 🔄 **Workflows de Desarrollo**

### **Desarrollo Local (Recomendado)**
```bash
# 1. Levantar solo infraestructura
make dev-up
# o
docker-compose -f docker-compose.dev.yml up -d

# 2. Ejecutar servicios localmente (en terminales separadas)
cd orders-service && ./mvnw spring-boot:run
cd products-service && ./mvnw spring-boot:run
cd notifications-service && ./mvnw spring-boot:run

# 3. Desarrollo con hot-reload automático
```

### **Testing en Docker**
```bash
# 1. Build completo
make build

# 2. Deploy en Docker
make up

# 3. Testing
curl http://localhost/health
```

### **Despliegue en Servidor**
```bash
# 1. Clonar código
git clone <repo> mini-shop
cd mini-shop

# 2. Configurar entorno
cp docker-compose.yml docker-compose.prod.yml
# Editar configuraciones de producción

# 3. Desplegar
./deploy.sh
```

---

## 🔒 **Configuración de Producción**

### **Variables de Entorno Recomendadas**
```yaml
# En docker-compose.prod.yml
environment:
  - SPRING_PROFILES_ACTIVE=production
  - JAVA_OPTS=-Xmx1g -Xms512m
  - NATS_URL=nats://nats-server:4222
  - LOG_LEVEL=WARN
```

### **Seguridad**
```yaml
# Exponer solo puertos necesarios
ports:
  - "80:80"    # Solo Nginx
  # Remover otros puertos expuestos

# Usar secretos para contraseñas
secrets:
  db_password:
    file: ./secrets/db_password.txt
```

### **Monitoreo**
```yaml
# Agregar herramientas de monitoreo
services:
  prometheus:
    image: prom/prometheus
    # ...
  
  grafana:
    image: grafana/grafana
    # ...
```

---

## 📡 **Configuración NATS Dedicado**

### **¿Por qué usar NATS dedicado?**

Tu Mini-Shop ahora usa una **instancia separada de NATS** en puertos diferentes:

- ✅ **Sin conflictos** con tu NATS existente (puerto 4222)
- ✅ **Aislamiento completo** para pruebas
- ✅ **Fácil debugging** y monitoreo dedicado
- ✅ **Flexibilidad** para desarrollo

### **Puertos del NATS Dedicado:**

| **Servicio** | **Puerto Externo** | **Puerto Interno** | **Uso** |
|--------------|-------------------|-------------------|---------|
| **NATS Client** | 8422 | 4222 | Conexiones de aplicaciones |
| **NATS Monitor** | 8423 | 8222 | Web UI de monitoreo |
| **NATS Cluster** | 8424 | 6222 | Comunicación cluster |

### **Opciones de Desarrollo:**

#### **Opción 1: Todo en Docker (Recomendado para testing)**
```bash
# Deploy completo
./deploy.sh
# o
make up
```

#### **Opción 2: Solo NATS en Docker + Servicios locales**
```bash
# Solo NATS
./start-nats-only.sh

# Servicios localmente (en terminales separadas)
export SPRING_PROFILES_ACTIVE=local-with-docker-nats
cd orders-service && ./mvnw spring-boot:run
cd products-service && ./mvnw spring-boot:run  
cd notifications-service && ./mvnw spring-boot:run
```

#### **Opción 3: Solo infraestructura (NATS + DB)**
```bash
# NATS + PostgreSQL + Redis + Adminer
make dev-up
```

### **Verificar NATS Dedicado:**

```bash
# Health check
curl http://localhost:8423/healthz

# Ver conexiones
curl http://localhost:8423/connz

# Ver subjects activos
curl http://localhost:8423/subsz

# Monitoreo web
open http://localhost:8423
```
