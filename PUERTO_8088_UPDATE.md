# 🔧 Actualización: Configuración Puerto 8088

## 📝 **Cambios Realizados**

### **Problema Solucionado**
- ✅ Servidor nuevo con Nginx existente en puerto 80
- ✅ Mini-Shop ahora corre en puerto 8088 (sin conflictos)

### **Archivos Actualizados**

#### **1. Scripts de Despliegue**
- `deploy-vpn.sh` → URLs actualizadas para puerto 8088
- `test-vpn-connectivity.sh` → Pruebas en puerto 8088

#### **2. Documentación**
- `VPN_SETUP_GUIDE.md` → Todas las URLs con puerto 8088
- `DOCKER_README.md` → Nueva sección "Puerto Alternativo (8088)"

#### **3. Makefile**
- Nuevos comandos VPN: `make vpn-up`, `make vpn-down`, `make vpn-info`
- Health checks específicos para puerto 8088

#### **4. Configuración Docker**
- `docker-compose.vpn.yml` → Ya estaba configurado correctamente

---

## 🚀 **Cómo Usar en el Nuevo Servidor**

### **Opción 1: Script Automático**
```bash
./deploy-vpn.sh
```

### **Opción 2: Makefile**
```bash
make vpn-up       # Desplegar en puerto 8088
make vpn-health   # Verificar servicios
make vpn-info     # Ver URLs de acceso
```

### **Opción 3: Docker Compose Manual**
```bash
docker compose -f docker-compose.vpn.yml up -d
```

---

## 🌐 **URLs de Acceso (Puerto 8088)**

- **Portal:** http://TU_SERVER_IP:8088
- **Orders:** http://TU_SERVER_IP:8088/orders-app
- **Products:** http://TU_SERVER_IP:8088/products
- **Notifications:** http://TU_SERVER_IP:8088/notifications-app
- **H2 Console:** http://TU_SERVER_IP:8088/h2-console
- **NATS Monitor:** http://TU_SERVER_IP:8423

---

## 🔥 **Firewall (Puertos Actualizados)**

```bash
sudo ufw allow 8088   # Nginx Mini-Shop
sudo ufw allow 8081   # Orders Service
sudo ufw allow 8082   # Products Service
sudo ufw allow 8083   # Notifications Service
sudo ufw allow 8422   # NATS Client
sudo ufw allow 8423   # NATS Monitor
```

---

## ✅ **Todo Listo**

Tu configuración está preparada para el nuevo servidor:
- ✅ Sin conflictos con Nginx existente
- ✅ Scripts actualizados
- ✅ Documentación actualizada
- ✅ Firewall configurado
- ✅ Health checks funcionando

¡Simplemente ejecuta `./deploy-vpn.sh` en tu nuevo servidor! 🎉
