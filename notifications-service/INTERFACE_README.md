# 📢 Notifications Service Interface

Una interfaz web moderna y en tiempo real para gestionar y visualizar notificaciones del sistema Mini Shop.

## 🌟 Características

### ✨ **Visualización en Tiempo Real**
- **Auto-refresh automático** cada 3 segundos
- **Notificaciones sonoras** cuando llegan nuevas notificaciones
- **Indicador visual** de notificaciones no leídas
- **Timestamps relativos** ("hace 5 min", "hace 2h")

### 🎯 **Gestión de Notificaciones**
- **Filtrado avanzado** por tipo y severidad
- **Vista solo no leídas** para focus en lo importante
- **Marcar como leído** individual o masivo
- **Contador de no leídas** en tiempo real

### 📊 **Dashboard y Estadísticas**
- **Estadísticas en vivo** (total, no leídas, por tipo)
- **Iconos visuales** para cada tipo de notificación
- **Colores por severidad** (Success, Warning, Error, Info)

### 🔧 **Herramientas de Desarrollo**
- **Crear notificaciones de prueba** para testing
- **Hot-reload** para desarrollo sin recompilación
- **API REST completa** para integración

## 📱 Tipos de Notificaciones

| Tipo | Icono | Descripción | Severidad Típica |
|------|-------|-------------|-----------------|
| `ORDER_CREATED` | 📦 | Nueva orden creada | SUCCESS |
| `ORDER_CANCELLED` | ❌ | Orden cancelada | WARNING |
| `LOW_STOCK` | 📉 | Stock bajo detectado | ERROR |
| `PAYMENT_CONFIRMED` | 💰 | Pago confirmado | SUCCESS |
| `DIRECT` | 📧 | Notificación directa | INFO |
| `TEST` | 🧪 | Notificación de prueba | Variable |

## 🎨 Colores por Severidad

- **🟢 SUCCESS**: Verde - Acciones exitosas
- **🔵 INFO**: Azul - Información general  
- **🟡 WARNING**: Naranja - Advertencias
- **🔴 ERROR**: Rojo - Errores críticos

## 🚀 Cómo Usar

### 1. **Acceder a la Interfaz**
```bash
# Asegúrate de que el servicio esté corriendo
cd notifications-service
./mvnw spring-boot:run

# Abre en el navegador
http://localhost:8083
```

### 2. **Filtrar Notificaciones**
- **Por Tipo**: Selecciona un tipo específico del dropdown
- **Por Severidad**: Filtra por nivel de importancia
- **Solo No Leídas**: Toggle para ver solo pendientes
- **Limpiar Filtros**: Botón para resetear todos los filtros

### 3. **Gestionar Estado**
- **Marcar como Leído**: Click en ✓ en cada notificación
- **Marcar Todo**: Botón "Mark All Read" en la barra superior
- **Auto-refresh**: Toggle para activar/desactivar actualización automática
- **Sonido**: Toggle para habilitar/deshabilitar notificaciones sonoras

### 4. **Crear Notificaciones de Prueba**
- Click en **"Create Test Notification"**
- Se genera automáticamente con tipo y severidad aleatorios
- Útil para probar la interfaz y el comportamiento en tiempo real

## 🔌 API Endpoints

### **GET** `/api/notifications`
- Obtiene todas las notificaciones
- **Response**: Array de notificaciones

### **GET** `/api/notifications/recent?limit=50`
- Obtiene las últimas N notificaciones
- **Query Params**: 
  - `limit`: Número máximo de notificaciones (default: 50)

### **GET** `/api/notifications/unread`
- Obtiene solo notificaciones no leídas
- **Response**: Array de notificaciones no leídas

### **GET** `/api/notifications/type/{type}`
- Filtra notificaciones por tipo específico
- **Path Params**: 
  - `type`: ORDER_CREATED, ORDER_CANCELLED, LOW_STOCK, etc.

### **GET** `/api/notifications/since?timestamp={iso-datetime}`
- Obtiene notificaciones desde una fecha específica
- **Query Params**: 
  - `timestamp`: ISO datetime (para polling)

### **PUT** `/api/notifications/{id}/read`
- Marca una notificación específica como leída
- **Path Params**: 
  - `id`: ID de la notificación

### **PUT** `/api/notifications/read-all`
- Marca todas las notificaciones como leídas
- **Response**: Confirmación de éxito

### **GET** `/api/notifications/stats`
- Obtiene estadísticas de notificaciones
- **Response**: 
  ```json
  {
    "total": 150,
    "unread": 5,
    "byType": {"ORDER_CREATED": 50, "LOW_STOCK": 10},
    "bySeverity": {"SUCCESS": 100, "ERROR": 20}
  }
  ```

### **POST** `/api/notifications/test`
- Crea una notificación de prueba
- **Form Params**:
  - `type`: Tipo de notificación
  - `title`: Título
  - `message`: Mensaje
  - `severity`: Nivel de severidad

## 🎯 Casos de Uso

### **Monitoreo en Tiempo Real**
1. Abre la interfaz en una pantalla secundaria
2. Activa auto-refresh y sonido
3. Las notificaciones aparecerán automáticamente
4. Los colores y sonidos te alertarán de eventos importantes

### **Gestión de Alertas**
1. Filtra por "ERROR" para ver solo problemas críticos
2. Usa "Unread only" para focus en pendientes
3. Marca como leído después de atender cada alerta
4. Usa estadísticas para ver tendencias

### **Testing y Desarrollo**
1. Usa "Create Test Notification" para generar datos
2. Prueba diferentes tipos y severidades
3. Verifica que el auto-refresh funcione correctamente
4. Testa filtros y funcionalidad de marcado

## 🔧 Configuración de Desarrollo

### **Hot-Reload**
Los archivos en `public/` se sirven directamente desde el sistema de archivos:
- **CSS**: Edita `public/css/notifications.css` y recarga
- **JS**: Edita `public/js/App.js` y recarga  
- **HTML**: Edita `public/index.html` y recarga

No necesitas recompilar el JAR para cambios en la interfaz.

### **Personalización**
- **Colores**: Modifica las variables CSS en `notifications.css`
- **Iconos**: Cambia los emojis en `getTypeIcon()` y `getSeverityIcon()`
- **Intervalos**: Ajusta `refreshInterval` en `App.js`
- **Límites**: Modifica `MAX_NOTIFICATIONS` en `NotificationRepository.java`

## 🌐 Responsive Design

La interfaz está optimizada para:
- **Desktop**: Experiencia completa con todos los features
- **Tablet**: Layout adaptado con filtros reorganizados
- **Mobile**: Vista mobile-first con navegación tactil optimizada

## 🔊 Notificaciones Sonoras

- **Beep suave** cuando llegan nuevas notificaciones
- **Toggle on/off** para ambientes silenciosos
- **Web Audio API** para compatibilidad cross-browser
- **No requiere plugins** o permisos especiales

## 📋 Próximas Características

- [ ] **WebSocket** para actualizaciones push en tiempo real
- [ ] **Filtros avanzados** por fecha y usuario
- [ ] **Exportar** notificaciones a CSV/Excel
- [ ] **Configuración** de intervalos de refresh
- [ ] **Dark mode** para uso nocturno
- [ ] **Notificaciones browser** nativas

## 🤝 Integración con Otros Servicios

Esta interfaz automáticamente muestra notificaciones de:
- **Orders Service**: Órdenes creadas/canceladas
- **Products Service**: Alertas de stock bajo
- **Payment Service**: Confirmaciones de pago
- **Cualquier servicio** que publique a NATS

¡La interfaz está lista para mostrar notificaciones en tiempo real según van ocurriendo en el sistema! 🚀
