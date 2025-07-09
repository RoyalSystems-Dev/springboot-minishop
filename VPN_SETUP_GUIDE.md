# 🌐 Mini-Shop VPN - Guía de Configuración

## 🚨 **Tu Problema Actual**

```
✅ Servidor VPN: curl localhost funciona
❌ Tu máquina:   curl 10.100.88.3 no funciona
```

**Causa:** Docker por defecto solo hace bind a `127.0.0.1` (localhost), no a todas las interfaces de red.

---

## 🔧 **Solución Implementada**

### **1. Configuración Docker VPN**

Creado `docker-compose.vpn.yml` con:

```yaml
services:
  nginx:
    ports:
      - "0.0.0.0:8088:80"    # ✅ Puerto 8088 (evita conflicto con Nginx existente)
  orders-service:
    ports:
      - "0.0.0.0:8081:8081"  # ✅ Bind a TODAS las interfaces
    environment:
      - SERVER_ADDRESS=0.0.0.0  # ✅ Spring Boot en todas las interfaces
```

### **2. Configuraciones Spring Boot VPN**

Archivos `application-docker-vpn.yml` con:

```yaml
server:
  port: 8081
  address: 0.0.0.0  # ✅ Escuchar en todas las interfaces

management:
  server:
    address: 0.0.0.0  # ✅ Actuator también accesible
```

---

## 🚀 **Despliegue en tu Servidor VPN**

### **En tu servidor Ubuntu (10.100.88.3):**

```bash
# 1. Clonar el proyecto
git clone <tu-repo> mini-shop
cd mini-shop

# 2. Desplegar con configuración VPN
./deploy-vpn.sh
```

### **El script automáticamente:**
- ✅ **Detecta** la IP del servidor
- ✅ **Configura** Docker para VPN
- ✅ **Verifica** firewall
- ✅ **Despliega** servicios
- ✅ **Muestra** URLs de acceso

---

## 🔍 **Verificar desde tu Máquina Local**

### **Opción 1: Script Automático**

```bash
# Desde tu máquina local
./test-vpn-connectivity.sh
```

### **Opción 2: Verificación Manual**

```bash
# Ping básico
ping 10.100.88.3

# Servicios web (Puerto 8088 para evitar conflicto con Nginx existente)
curl http://10.100.88.3:8088/health
curl http://10.100.88.3:8081/actuator/health
curl http://10.100.88.3:8083/actuator/health

# NATS monitoring
curl http://10.100.88.3:8423/healthz
```

---

## 🔥 **Configuración de Firewall**

### **En el servidor Ubuntu:**

```bash
# Verificar estado
sudo ufw status

# Habilitar puertos necesarios
sudo ufw allow 8088        # Nginx (Mini-Shop - Puerto no conflictivo)
sudo ufw allow 8081        # Orders Service
sudo ufw allow 8082        # Products Service  
sudo ufw allow 8083        # Notifications Service
sudo ufw allow 8422        # NATS Client
sudo ufw allow 8423        # NATS Monitoring

# Verificar reglas aplicadas
sudo ufw status numbered
```

---

## 🌐 **URLs de Acceso**

Una vez desplegado, desde tu máquina local:

| **Servicio** | **URL** | **Descripción** |
|--------------|---------|-----------------|
| **Portal** | http://10.100.88.3:8088 | Página principal |
| **Orders** | http://10.100.88.3:8088/orders-app | Interfaz de órdenes |
| **Products** | http://10.100.88.3:8088/products | API de productos |
| **Notifications** | http://10.100.88.3:8088/notifications-app | Interfaz de notificaciones |
| **H2 Console** | http://10.100.88.3:8088/h2-console | Base de datos |
| **NATS Monitor** | http://10.100.88.3:8423 | Monitoreo NATS |

---

## 🐛 **Troubleshooting**

### **1. No hay conectividad básica**

```bash
# Verificar VPN
ping 10.100.88.3

# Si falla: verificar conexión VPN
```

### **2. Ping funciona pero HTTP no**

```bash
# En el servidor, verificar puertos
sudo netstat -tulpn | grep :8088
sudo netstat -tulpn | grep :8081

# Verificar Docker
docker ps
docker compose -f docker-compose.vpn.yml logs nginx
```

### **3. Docker escucha solo en localhost**

```bash
# Verificar binding
sudo netstat -tulpn | grep :8088

# Debería mostrar: 0.0.0.0:8088, NO 127.0.0.1:8088
```

### **4. Firewall bloqueando**

```bash
# Verificar logs de firewall
sudo tail -f /var/log/ufw.log

# Temporalmente deshabilitar para probar
sudo ufw disable
# Probar conectividad
# Volver a habilitar
sudo ufw enable
```

### **5. Spring Boot en localhost**

```bash
# Verificar configuración
docker compose -f docker-compose.vpn.yml exec orders-service env | grep SERVER_ADDRESS

# Debería mostrar: SERVER_ADDRESS=0.0.0.0
```

---

## 📊 **Diferencias vs Configuración Local**

| **Aspecto** | **Local** | **VPN** |
|-------------|-----------|---------|
| **Docker Bind** | `127.0.0.1` | `0.0.0.0` |
| **Spring Boot** | `localhost` | `0.0.0.0` |
| **Firewall** | No necesario | Requerido |
| **Acceso** | Solo local | Desde VPN |

---

## 🎯 **Pasos de Verificación**

### **En el Servidor:**

1. **Desplegar:**
   ```bash
   ./deploy-vpn.sh
   ```

2. **Verificar localmente:**
   ```bash
   curl localhost:8088/health
   curl localhost:8081/actuator/health
   ```

3. **Verificar binding:**
   ```bash
   sudo netstat -tulpn | grep :8088
   # Debe mostrar: 0.0.0.0:8088
   ```

### **Desde tu Máquina:**

1. **Conectividad básica:**
   ```bash
   ping 10.100.88.3
   ```

2. **Servicios:**
   ```bash
   curl http://10.100.88.3:8088/health
   ```

3. **Script completo:**
   ```bash
   ./test-vpn-connectivity.sh
   ```

---

## 💡 **Tips Adicionales**

### **Para desarrollo continuo:**
- Usa `docker-compose.vpn.yml` siempre en el servidor
- Mantén `docker-compose.yml` para desarrollo local
- Los logs están disponibles: `docker compose -f docker-compose.vpn.yml logs -f`

### **Para monitoreo:**
- NATS: http://10.100.88.3:8423
- Health checks: http://10.100.88.3:8088/health
- Logs individuales: `docker compose -f docker-compose.vpn.yml logs -f [servicio]`

---

¡Con esta configuración podrás acceder a Mini-Shop desde tu máquina local a través de la VPN! 🚀
