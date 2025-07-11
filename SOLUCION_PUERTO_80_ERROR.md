# 🔧 Solución: Error Puerto 80 en Mini-Shop

## ❌ **Problema Resuelto**
```
Error response from daemon: failed to bind host port for 0.0.0.0:80:172.18.0.6:80/tcp: address already in use
```

## ✅ **Cambios Realizados**

### **1. Docker Compose Principal Actualizado**
- `docker-compose.yml` → Ahora usa puerto 8088 (en lugar de 80)
- `docker-compose.port80.yml` → Versión original puerto 80 (respaldo)

### **2. Script deploy.sh Actualizado**
- ✅ Bug línea 107 corregido: `$DOCKER_COMPOSE_CMD` en lugar de `DOCKER_COMPOSE_CMD`
- ✅ URLs actualizadas para puerto 8088
- ✅ Health checks actualizados para puerto 8088
- ✅ Información adicional sobre configuración

### **3. Configuración Nginx**
- ✅ `nginx/conf.d/mini-shop.conf` ya tenía configuración puerto 8088
- ✅ Servidor dual: puerto 80 + puerto 8088

---

## 🚀 **Cómo Usar Ahora**

### **Opción 1: Puerto 8088 (SIN conflictos) - RECOMENDADO**
```bash
# Usar configuración principal (ahora puerto 8088)
./deploy.sh
# Seleccionar opción 2: Solo desplegar
```

### **Opción 2: Puerto 80 (Solo si no hay conflictos)**
```bash
# Usar configuración puerto 80 original
docker compose -f docker-compose.port80.yml up -d
```

### **Opción 3: VPN/DMZ (Puerto 8088 con bind externo)**
```bash
# Para servidores remotos
./deploy-vpn.sh
```

---

## 🌐 **URLs de Acceso (Puerto 8088)**

- **Portal:** http://localhost:8088
- **Orders:** http://localhost:8088/orders-app
- **Products:** http://localhost:8088/products
- **Notifications:** http://localhost:8088/notifications-app
- **H2 Console:** http://localhost:8088/h2-console
- **NATS Monitor:** http://localhost:8423

---

## 📊 **Archivos Disponibles**

| Archivo | Puerto | Uso |
|---------|--------|-----|
| `docker-compose.yml` | 8088 | **Principal (sin conflictos)** |
| `docker-compose.port80.yml` | 80 | Original (solo sin conflictos) |
| `docker-compose.vpn.yml` | 8088 | VPN/DMZ con bind externo |
| `docker-compose.dev.yml` | - | Solo infraestructura |

---

## 🔧 **Verificación**

### **Verificar puertos libres:**
```bash
# Linux/Mac
sudo netstat -tulpn | grep :80
sudo netstat -tulpn | grep :8088

# Windows
netstat -an | findstr :80
netstat -an | findstr :8088
```

### **Probar acceso:**
```bash
# Después del despliegue
curl http://localhost:8088/health
```

---

## 💡 **Recomendaciones**

1. **Usa puerto 8088** (configuración principal actualizada)
2. **Puerto 80 solo** si no hay otros servicios web
3. **Para VPN/DMZ** usa `deploy-vpn.sh`
4. **Para desarrollo** usa `docker-compose.dev.yml`

---

¡El error del puerto 80 está resuelto! Ahora Mini-Shop usa puerto 8088 por defecto. 🎉
