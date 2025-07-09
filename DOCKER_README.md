# 🐳 Mini-Shop - Guía de Docker

## 📋 **Resumen de la Arquitectura**

```
🌐 Nginx (Puerto 80)
    ↓
📦 Orders Service (Puerto 8081)
📦 Products Service (Porto 8082)  
📦 Notifications Service (Puerto 8083)
    ↓
📡 NATS Server (Puerto 4222)
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
| **NATS Monitor** | 8222 | http://localhost:8222 | Monitoreo NATS |

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
# Monitoring web de NATS
curl http://localhost:8222/connz

# Ver subjects activos
curl http://localhost:8222/subsz
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

## 📚 **Recursos Adicionales**

- 📖 [Docker Compose Documentation](https://docs.docker.com/compose/)
- 🌐 [NATS Documentation](https://docs.nats.io/)
- 🍃 [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- 🔧 [Nginx Configuration](https://nginx.org/en/docs/)

---

## 🎯 **Próximos Pasos**

1. **✅ Configurar CI/CD** con GitHub Actions
2. **✅ Agregar monitoreo** con Prometheus + Grafana
3. **✅ Implementar logs centralizados** con ELK Stack
4. **✅ Configurar backup** de volúmenes
5. **✅ Agregar certificados SSL** para HTTPS
