# Configuración de NATS para Orders Service

## Descripción

Este documento describe la configuración e implementación de NATS (Neural Autonomic Transport System) en el servicio de órdenes del mini-shop.

## Configuración

### 1. Dependencias

La dependencia de NATS ya está incluida en el `pom.xml`:

```xml
<dependency>
    <groupId>io.nats</groupId>
    <artifactId>jnats</artifactId>
    <version>2.14.1</version>
</dependency>
```

### 2. Configuración en application.yml

El archivo `config/application.yml` contiene toda la configuración necesaria para NATS:

```yaml
# NATS Configuration
nats:
  url: nats://localhost:4222
  connection:
    timeout: 2000
    max-reconnect: 60
    reconnect-wait: 2000
    ping-interval: 120000
    cleanup-interval: 30000
  consumer:
    max-deliver: 3
    ack-wait: 30000
  producer:
    max-pending: 1000
    timeout: 2000
  cluster:
    enabled: false

# Configuración de subjects
messaging:
  subjects:
    orders:
      created: "orders.created"
      updated: "orders.updated" 
      cancelled: "orders.cancelled"
      status-changed: "orders.status.changed"
    notifications:
      send: "notifications.send"
    products:
      check-inventory: "products.inventory.check"
      update-inventory: "products.inventory.update"
    payments:
      process: "payments.process"
      confirmed: "payments.confirmed"
      failed: "payments.failed"
```

## Clases de Configuración

### 1. NatsProperties.java
- Mapea las propiedades de configuración de NATS
- Incluye configuración de conexión, consumer, producer y cluster

### 2. MessagingProperties.java  
- Mapea los subjects de mensajería para diferentes servicios
- Organiza los subjects por categorías (orders, notifications, products, payments)

### 3. NatsConfig.java
- Configuración principal de NATS
- Establece la conexión con el servidor NATS
- Maneja eventos de conexión/desconexión

## Servicios

### 1. NatsService.java
- Servicio base para operaciones con NATS
- Métodos para publicar mensajes, enviar requests y suscribirse a subjects
- Serialización/deserialización automática a JSON

### 2. OrderEventService.java
- Servicio específico para eventos de órdenes
- Maneja la publicación de eventos de órdenes (creada, actualizada, cancelada, cambio de estado)
- Se suscribe a eventos de pagos
- Envía notificaciones automáticamente

## DTOs

### 1. OrderEvent.java
- DTO para eventos de órdenes
- Contiene información del evento (orderId, userId, status, action, timestamp, data)

### 2. NotificationMessage.java
- DTO para mensajes de notificación
- Contiene información del mensaje (userId, message, type, timestamp, channel, metadata)

## Uso en el Controlador

El `OrderController` ha sido actualizado para usar NATS:

- **POST /orders**: Publica evento `orders.created` y envía notificación
- **PUT /orders/{id}**: Publica evento `orders.updated`  
- **DELETE /orders/{id}**: Publica evento `orders.cancelled` y envía notificación
- **PUT /orders/{id}/status/{status}**: Publica evento `orders.status.changed`

## Instalación y Ejecución

### 1. Instalar NATS Server

```bash
# Usando Docker
docker run -p 4222:4222 -ti nats:latest

# O descargar desde https://nats.io/download/
```

### 2. Iniciar el servicio

```bash
./mvnw spring-boot:run
```

### 3. Verificar conectividad

Al iniciar la aplicación, deberías ver en los logs:
```
Connected to NATS server: nats://localhost:4222
```

## Ejemplos de Uso

### Crear una orden (genera eventos NATS)

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "quantity": 1
  }'
```

### Cambiar estado de una orden

```bash
curl -X PUT http://localhost:8081/orders/1/status/PROCESSING
```

## Subjects Disponibles

### Órdenes
- `orders.created`: Cuando se crea una orden
- `orders.updated`: Cuando se actualiza una orden
- `orders.cancelled`: Cuando se cancela una orden
- `orders.status.changed`: Cuando cambia el estado de una orden

### Notificaciones
- `notifications.send`: Para enviar notificaciones

### Productos  
- `products.inventory.check`: Para verificar inventario
- `products.inventory.update`: Para actualizar inventario

### Pagos
- `payments.process`: Para procesar pagos
- `payments.confirmed`: Confirmación de pago
- `payments.failed`: Fallo en el pago

## Monitoreo

Para monitorear los mensajes NATS, puedes usar:

```bash
# Suscribirse a todos los eventos de órdenes
nats sub "orders.*"

# Suscribirse a un evento específico
nats sub "orders.created"
```

## Notas Importantes

- El servidor NATS debe estar ejecutándose en `localhost:4222` (por defecto)
- Los mensajes se serializan automáticamente a JSON
- La configuración de cluster está deshabilitada por defecto
- Los handlers de suscripción se ejecutan en hilos separados
- Se incluye reconexión automática en caso de pérdida de conexión

## Configuración para Producción

Para producción, considera:

1. Habilitar cluster NATS para alta disponibilidad
2. Configurar autenticación y autorización
3. Usar TLS para comunicación segura
4. Ajustar timeouts según las necesidades
5. Implementar dead letter queues para mensajes fallidos
6. Usar streaming (NATS JetStream) para persistencia de mensajes
