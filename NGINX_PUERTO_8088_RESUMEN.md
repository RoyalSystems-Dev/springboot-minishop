# 🔧 Configuración Nginx Puerto 8088 - Resumen de Cambios

## 📝 **Archivos Modificados/Creados**

### **1. Configuración Nginx Actualizada**
- ✅ `nginx/conf.d/mini-shop.conf` → Añadido servidor puerto 8088
- ✅ Configuración dual: Puerto 80 (original) + Puerto 8088 (sin conflictos)

### **2. Scripts de Despliegue**
- ✅ `deploy-vpn.bat` → Nuevo script Windows para VPN (puerto 8088)
- ✅ `deploy.bat` → Actualizado con soporte Docker Compose automático
- ✅ `docker-compose.port8088.yml` → Nueva configuración puerto 8088

### **3. Configuración Docker Compose**
- ✅ `docker-compose.vpn.yml` → Ya configurado para puerto 8088
- ✅ `docker-compose.port8088.yml` → Nueva opción independiente

---

## 🌐 **Configuración del Puerto 8088**

### **Nginx Configuration**
```nginx
# Servidor Principal (Puerto 80)
server {
    listen 80;
    server_name localhost mini-shop.local;
    # ... configuración completa
}

# Servidor Puerto 8088 (Sin conflictos)
server {
    listen 8088;
    server_name localhost mini-shop.local;
    # ... misma configuración pero puerto diferente
}
```

### **Docker Compose**
```yaml
nginx:
  ports:
    - "8088:8088"    # Puerto 8088 externo e interno
    # o
    - "0.0.0.0:8088:8088"  # Para acceso VPN/externo
```

---

## 🚀 **Cómo Usar**

### **Opción 1: Script VPN Windows**
```cmd
deploy-vpn.bat
```

### **Opción 2: Docker Compose Puerto 8088**
```bash
# Windows
docker-compose -f docker-compose.port8088.yml up -d

# Linux
docker compose -f docker-compose.port8088.yml up -d
```

### **Opción 3: Configuración VPN (Recomendada)**
```bash
# Windows
docker-compose -f docker-compose.vpn.yml up -d

# Linux
docker compose -f docker-compose.vpn.yml up -d
```

---

## 🔗 **URLs de Acceso**

### **Puerto 8088 (Sin conflictos)**
- **Portal:** http://localhost:8088
- **Orders:** http://localhost:8088/orders-app
- **Products:** http://localhost:8088/products
- **Notifications:** http://localhost:8088/notifications-app
- **H2 Console:** http://localhost:8088/h2-console
- **Health Check:** http://localhost:8088/health

### **Puerto 80 (Original)**
- **Portal:** http://localhost
- **Orders:** http://localhost/orders-app
- **Products:** http://localhost/products
- **Notifications:** http://localhost/notifications-app
- **H2 Console:** http://localhost/h2-console
- **Health Check:** http://localhost/health

---

## 🔥 **Firewall (Para Servidor)**

```bash
# Permitir puerto 8088
sudo ufw allow 8088

# Verificar
sudo ufw status
sudo netstat -tulpn | grep :8088
```

---

## 📊 **Comandos Útiles**

### **Ver Logs**
```cmd
REM Windows
docker-compose -f docker-compose.vpn.yml logs -f

REM Linux
docker compose -f docker-compose.vpn.yml logs -f
```

### **Estado de Servicios**
```cmd
REM Windows
docker-compose -f docker-compose.vpn.yml ps

REM Linux
docker compose -f docker-compose.vpn.yml ps
```

### **Reiniciar Servicios**
```cmd
REM Windows
docker-compose -f docker-compose.vpn.yml restart

REM Linux
docker compose -f docker-compose.vpn.yml restart
```

---

## ✅ **Beneficios del Puerto 8088**

1. **Sin Conflictos:** No interfiere con Nginx existente en puerto 80
2. **Fácil Migración:** Mismo código, solo cambia el puerto
3. **Acceso Dual:** Puedes tener ambos puertos activos
4. **Compatibilidad:** Funciona en Windows y Linux
5. **Scripts Listos:** Automatización completa

---

## 🎯 **Casos de Uso**

### **Servidor con Nginx Existente**
```bash
# Usa puerto 8088 para evitar conflictos
./deploy-vpn.sh
# o
deploy-vpn.bat
```

### **Servidor Limpio**
```bash
# Usa puerto 80 (configuración original)
./deploy.sh
# o
deploy.bat
```

### **Desarrollo Local**
```bash
# Cualquier puerto funciona
make up          # Puerto 80
make vpn-up      # Puerto 8088
```

---

¡Configuración completada! Tu Mini-Shop ahora puede funcionar en ambos puertos sin conflictos. 🚀
