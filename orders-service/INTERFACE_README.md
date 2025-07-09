# 🛒 Mini Shop - Gestión de Órdenes

Una interfaz web moderna para gestionar órdenes en el sistema Mini Shop, construida con Vue.js 3 y Spring Boot.

## 🚀 Características

### Funcionalidades Principales
- ✅ **Crear nuevas órdenes** - Interfaz intuitiva para crear órdenes con nombre de producto y cantidad
- 📋 **Listar todas las órdenes** - Vista en tarjetas con información completa
- ✏️ **Editar órdenes** - Modificar órdenes existentes
- 🗑️ **Eliminar órdenes** - Eliminar órdenes con confirmación
- 🔄 **Actualizar estado** - Cambiar estado de órdenes (Pendiente, Completado, Cancelado)
- 🔄 **Actualización en tiempo real** - Refrescar datos automáticamente
- 📱 **Diseño responsive** - Funciona perfectamente en móviles y escritorio

### Interfaz de Usuario
- 🎨 **Diseño moderno** - Interfaz limpia y profesional con gradientes
- 💫 **Animaciones suaves** - Transiciones fluidas y efectos hover
- 🔔 **Notificaciones** - Mensajes de éxito y error claros
- ⚡ **Carga rápida** - Indicadores de estado de carga
- 🌟 **Experiencia intuitiva** - Navegación fácil y accesible

## 🛠️ Tecnologías

### Frontend
- **Vue.js 3** - Framework progresivo con Composition API
- **CSS3** - Estilos modernos con Flexbox y Grid
- **Axios** - Cliente HTTP para comunicación con API
- **JavaScript ES6+** - Sintaxis moderna

### Backend
- **Spring Boot 3.5.3** - Framework de aplicaciones Java
- **Spring Web** - Para crear REST APIs
- **NATS** - Sistema de mensajería para comunicación entre servicios
- **H2 Database** - Base de datos en memoria para desarrollo

## 📋 API Endpoints

La aplicación consume los siguientes endpoints REST:

### Órdenes
- `GET /orders` - Obtener todas las órdenes
- `GET /orders/{id}` - Obtener orden por ID
- `POST /orders` - Crear nueva orden
- `PUT /orders/{id}` - Actualizar orden
- `DELETE /orders/{id}` - Eliminar orden
- `PUT /orders/{id}/status/{status}` - Actualizar estado de orden

### Estructura de Datos
```json
{
  "id": 1,
  "productName": "Product-1",
  "quantity": 2
}
```

## 🚀 Instrucciones de Uso

### Iniciar el Servidor
1. Navegar al directorio del servicio de órdenes:
   ```bash
   cd orders-service
   ```

2. Ejecutar el servidor Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

3. El servidor estará disponible en: `http://localhost:8081`

### Usar la Interfaz Web
1. Abrir navegador y ir a: `http://localhost:8081`
2. La interfaz se cargará automáticamente

### Funcionalidades Disponibles

#### ➕ Crear Nueva Orden
1. Hacer clic en el botón "➕ Nueva Orden"
2. Llenar el formulario:
   - **Nombre del Producto**: Ingrese el nombre del producto
   - **Cantidad**: Especifique la cantidad (mínimo 1)
3. Hacer clic en "Crear Orden"

#### 📋 Ver Órdenes
- Las órdenes se muestran en tarjetas con:
  - ID de la orden
  - Nombre del producto
  - Cantidad
  - Botones de acción

#### ✏️ Editar Orden
1. Hacer clic en el botón "✏️" en la tarjeta de la orden
2. Modificar los datos en el formulario
3. Hacer clic en "Actualizar Orden"

#### 🗑️ Eliminar Orden
1. Hacer clic en el botón "🗑️" en la tarjeta de la orden
2. Confirmar la eliminación en el diálogo

#### 🔄 Cambiar Estado
Use los botones de estado en cada tarjeta:
- **⏳ Pendiente** - Marcar como pendiente
- **✅ Completado** - Marcar como completado
- **❌ Cancelado** - Marcar como cancelado

#### 🔄 Refrescar Datos
- Hacer clic en "🔄 Refrescar" para actualizar la lista

## 📂 Estructura del Proyecto

```
orders-service/
├── src/main/
│   ├── java/com/minishop/ordersservice/
│   │   ├── config/
│   │   │   ├── WebConfig.java          # Configuración web
│   │   │   ├── NatsConfig.java         # Configuración NATS
│   │   │   └── MessagingProperties.java
│   │   ├── controller/
│   │   │   └── OrderController.java    # REST API endpoints
│   │   ├── dto/
│   │   │   ├── OrderDto.java          # Modelo de datos
│   │   │   └── OrderEvent.java
│   │   └── service/
│   │       └── OrderEventService.java
│   └── resources/
│       ├── static/                     # Archivos web estáticos
│       │   ├── index.html             # Página principal
│       │   ├── css/
│       │   │   └── app.css            # Estilos
│       │   └── js/
│       │       ├── main.js            # Inicialización Vue
│       │       └── App.js             # Componente principal
│       └── application.properties
└── public/                            # Archivos fuente (desarrollo)
    ├── index.html
    ├── css/app.css
    └── js/
        ├── main.js
        └── App.js
```

## 🎨 Características del Diseño

### Colores y Temas
- **Gradiente principal**: Azul a púrpura (#667eea → #764ba2)
- **Botones**: Diferentes colores para cada acción
- **Estados**: Colores semánticos (verde=éxito, rojo=error, etc.)

### Responsive Design
- **Móvil**: Diseño de una columna, botones apilados
- **Tablet**: Grid de 2 columnas
- **Escritorio**: Grid flexible hasta 3+ columnas

### Animaciones
- **Hover effects**: Elevación de tarjetas y botones
- **Loading**: Spinner animado durante operaciones
- **Transiciones**: Suaves entre estados

## 🔧 Personalización

### Modificar Estilos
Editar `src/main/resources/static/css/app.css` para cambiar:
- Colores del tema
- Tipografía
- Espaciado
- Animaciones

### Agregar Funcionalidades
Modificar `src/main/resources/static/js/App.js` para:
- Nuevas operaciones
- Validaciones adicionales
- Integraciones con otros servicios

## 🚨 Troubleshooting

### Problemas Comunes

#### Error de Conexión
- Verificar que el servidor esté ejecutándose en puerto 8081
- Revisar logs del servidor para errores

#### Interfaz no se Carga
- Verificar que los archivos estén en `src/main/resources/static/`
- Comprobar configuración en `WebConfig.java`

#### Errores de CORS
- El servidor está configurado para servir archivos estáticos
- Si usa dominio diferente, configurar CORS en Spring Boot

### Logs del Servidor
```bash
# Ver logs en tiempo real
./mvnw spring-boot:run

# Compilar sin ejecutar
./mvnw clean compile
```

## 📞 Soporte

Este sistema es parte del proyecto Mini Shop y usa:
- **Puerto**: 8081
- **Base de datos**: H2 (en memoria)
- **Mensajería**: NATS (puerto 4222)

---

**¡Disfruta gestionando tus órdenes con esta interfaz moderna! 🚀**
