# 🛍️ Mini Shop - Gestión de Productos

Una interfaz web moderna para gestionar productos en el sistema Mini Shop, construida con Vue.js 3 y Spring Boot.

## 🚀 Características

### Funcionalidades Principales
- ✅ **Crear nuevos productos** - Interfaz intuitiva para crear productos con nombre y precio
- 📋 **Listar todos los productos** - Vista en tarjetas con información completa
- ✏️ **Editar productos** - Modificar productos existentes
- 🗑️ **Eliminar productos** - Eliminar productos con confirmación
- ⚠️ **Simular stock bajo** - Activar alertas de stock bajo para testing
- 🔄 **Actualización en tiempo real** - Refrescar datos automáticamente
- 📱 **Diseño responsive** - Funciona perfectamente en móviles y escritorio

### Interfaz de Usuario
- 🎨 **Diseño moderno** - Interfaz limpia y profesional con tema verde
- 💫 **Animaciones suaves** - Transiciones fluidas y efectos hover
- 🔔 **Notificaciones** - Mensajes de éxito y error claros
- ⚡ **Carga rápida** - Indicadores de estado de carga
- 🌟 **Experiencia intuitiva** - Navegación fácil y accesible
- 💰 **Formato de moneda** - Precios formateados automáticamente en euros

## 🛠️ Tecnologías

### Frontend
- **Vue.js 3** - Framework progresivo con Composition API
- **CSS3** - Estilos modernos con Flexbox y Grid (tema verde)
- **Axios** - Cliente HTTP para comunicación con API
- **JavaScript ES6+** - Sintaxis moderna

### Backend
- **Spring Boot 3.5.3** - Framework de aplicaciones Java
- **Spring Web** - Para crear REST APIs
- **NATS** - Sistema de mensajería para comunicación entre servicios
- **H2 Database** - Base de datos en memoria para desarrollo

## 📋 API Endpoints

La aplicación consume los siguientes endpoints REST:

### Productos
- `GET /products` - Obtener todos los productos
- `GET /products/{id}` - Obtener producto por ID
- `POST /products` - Crear nuevo producto
- `PUT /products/{id}` - Actualizar producto
- `DELETE /products/{id}` - Eliminar producto
- `POST /products/{id}/low-stock` - Simular stock bajo (para testing)

### Estructura de Datos
```json
{
  "id": 1,
  "name": "Product-1",
  "price": 100.0
}
```

## 🚀 Instrucciones de Uso

### Iniciar el Servidor
1. Navegar al directorio del servicio de productos:
   ```bash
   cd products-service
   ```

2. Ejecutar el servidor Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

3. El servidor estará disponible en: `http://localhost:8082`

### Usar la Interfaz Web
1. Abrir navegador y ir a: `http://localhost:8082`
2. La interfaz se cargará automáticamente

### Funcionalidades Disponibles

#### ➕ Crear Nuevo Producto
1. Hacer clic en el botón "➕ Nuevo Producto"
2. Llenar el formulario:
   - **Nombre del Producto**: Ingrese el nombre del producto
   - **Precio (€)**: Especifique el precio (acepta decimales)
3. Hacer clic en "Crear Producto"

#### 📋 Ver Productos
- Los productos se muestran en tarjetas con:
  - ID del producto
  - Nombre del producto
  - Precio formateado en euros
  - Botones de acción

#### ✏️ Editar Producto
1. Hacer clic en el botón "✏️" en la tarjeta del producto
2. Modificar los datos en el formulario
3. Hacer clic en "Actualizar Producto"

#### 🗑️ Eliminar Producto
1. Hacer clic en el botón "🗑️" en la tarjeta del producto
2. Confirmar la eliminación en el diálogo

#### ⚠️ Simular Stock Bajo
1. Hacer clic en el botón "⚠️ Stock Bajo" en la tarjeta del producto
2. Se enviará un evento NATS de stock bajo (para testing del sistema de notificaciones)
3. Aparecerá una notificación de confirmación

#### 🔄 Refrescar Datos
- Hacer clic en "🔄 Refrescar" para actualizar la lista

## 📂 Estructura del Proyecto

```
products-service/
├── src/main/
│   ├── java/com/minishop/productsservice/
│   │   ├── config/
│   │   │   ├── WebConfig.java          # Configuración web
│   │   │   ├── NatsConfig.java         # Configuración NATS
│   │   │   └── MessagingProperties.java
│   │   ├── controller/
│   │   │   └── ProductController.java  # REST API endpoints
│   │   ├── dto/
│   │   │   ├── ProductDto.java        # Modelo de datos
│   │   │   └── ProductEvent.java
│   │   └── service/
│   │       └── ProductEventService.java
│   └── resources/
│       ├── static/                     # Archivos web estáticos
│       │   ├── index.html             # Página principal
│       │   ├── css/
│       │   │   └── app.css            # Estilos (tema verde)
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
- **Gradiente principal**: Fondo azul a púrpura
- **Tema de productos**: Verde esmeralda (#10b981) para elementos principales
- **Estados**: Colores semánticos (verde=éxito, amarillo=advertencia, rojo=error)
- **Precios**: Verde destacado para buena legibilidad

### Diferencias con Orders Service
- **Color primario**: Verde en lugar de azul/púrpura
- **Borde de tarjetas**: Borde izquierdo verde para identificación
- **Precio destacado**: Formato de moneda europeo con estilo especial
- **Funcionalidad especial**: Botón para simular stock bajo

### Responsive Design
- **Móvil**: Diseño de una columna, botones apilados
- **Tablet**: Grid de 2 columnas
- **Escritorio**: Grid flexible hasta 3+ columnas

### Animaciones
- **Hover effects**: Elevación de tarjetas y botones
- **Loading**: Spinner animado con colores del tema
- **Transiciones**: Suaves entre estados

## 🔧 Personalización

### Modificar Estilos
Editar `src/main/resources/static/css/app.css` para cambiar:
- Colores del tema (actualmente verde esmeralda)
- Tipografía
- Espaciado
- Animaciones

### Agregar Funcionalidades
Modificar `src/main/resources/static/js/App.js` para:
- Nuevas operaciones de productos
- Validaciones adicionales
- Integraciones con inventario
- Categorías de productos

## 💰 Formato de Precios

La aplicación formatea automáticamente los precios usando:
- **Moneda**: Euro (€)
- **Locale**: es-ES (español)
- **Formato**: 123,45 €

Para cambiar la moneda o formato, modificar la función `formatPrice` en `App.js`.

## 🚨 Troubleshooting

### Problemas Comunes

#### Error de Conexión
- Verificar que el servidor esté ejecutándose en puerto 8082
- Revisar logs del servidor para errores
- Comprobar que no haya conflictos de puerto

#### Interfaz no se Carga
- Verificar que los archivos estén en `src/main/resources/static/`
- Comprobar configuración en `WebConfig.java`
- Revisar configuración de recursos estáticos en `application.yml`

#### Errores de CORS
- El servidor está configurado para servir archivos estáticos
- Si usa dominio diferente, configurar CORS en Spring Boot

#### Formato de Precios Incorrecto
- Verificar que el navegador soporte `Intl.NumberFormat`
- Revisar la configuración de locale en `formatPrice`

### Logs del Servidor
```bash
# Ver logs en tiempo real
./mvnw spring-boot:run

# Compilar sin ejecutar
./mvnw clean compile

# Ejecutar tests
./mvnw test
```

## 🔗 Integración con Sistema

### Eventos NATS Publicados
- `products.created` - Cuando se crea un producto
- `products.updated` - Cuando se actualiza un producto
- `products.deleted` - Cuando se elimina un producto
- `products.stock.low` - Cuando se simula stock bajo

### Conectividad
- **Puerto**: 8082
- **Base de datos**: H2 (en memoria)
- **Mensajería**: NATS (puerto 4222)
- **Integración**: Con orders-service y notifications-service

## 📞 Soporte

Este sistema es parte del proyecto Mini Shop y se coordina con:
- **Orders Service** (puerto 8081) - Para gestión de órdenes
- **Notifications Service** (puerto 8083) - Para notificaciones
- **NATS Server** (puerto 4222) - Para mensajería entre servicios

---

**¡Disfruta gestionando tus productos con esta interfaz moderna! 🛍️**
