# 🗄️ Guía Completa: Spring Boot + H2 + JPA

## 🤔 ¿Por qué no ves configuración?

Spring Boot usa **Auto-Configuration** (configuración automática). Cuando encuentra estas dependencias en tu `pom.xml`:

```xml
<!-- JPA/Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Base de datos H2 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Automáticamente configura:**
- 🗄️ H2 Database en memoria
- 🔗 Pool de conexiones
- 🏗️ Hibernate como ORM
- 📝 Auto-creación de tablas

---

## 🔧 **Configuración Implícita vs Explícita**

### **🟢 Configuración Actual (Implícita)**
```yaml
# application.yml - SIN configuración de BD
spring:
  application:
    name: notifications-service
# ¡Spring Boot configura H2 automáticamente!
```

### **🔵 Configuración Explícita (Opcional)**
```yaml
# Si quisieras ser explícito:
spring:
  application:
    name: notifications-service
  
  # H2 Database Configuration  
  datasource:
    url: jdbc:h2:mem:testdb          # BD en memoria
    driverClassName: org.h2.Driver
    username: sa
    password: password
    
  # JPA/Hibernate Configuration
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop          # Crear/eliminar tablas
    show-sql: true                   # Mostrar SQL en logs
    
  # H2 Console (para ver la BD)
  h2:
    console:
      enabled: true                  # Habilitar consola web
      path: /h2-console
```

---

## 🚀 **Cómo Usar H2 con JPA - Ejemplo Práctico**

Actualmente usas un **Repository en memoria**. Te muestro cómo cambiarlo a **JPA + H2**:

### **1. Entidad JPA (En lugar de modelo simple)**

```java
// src/main/java/.../model/Notification.java
package com.minishop.notificationsservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity                                    // ← JPA Entity
@Table(name = "notifications")             // ← Nombre de tabla
public class Notification {
    
    @Id                                   // ← Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-increment
    private Long id;
    
    @Column(name = "notification_type", nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")     // ← Para mensajes largos
    private String message;
    
    @Column(name = "created_at")
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private String severity;
    
    @Column(name = "is_read")
    private boolean read = false;
    
    // Constructores
    public Notification() {
        this.timestamp = LocalDateTime.now();
    }
    
    public Notification(String type, String title, String message, String severity) {
        this();
        this.type = type;
        this.title = title;
        this.message = message;
        this.severity = severity;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
```

### **2. Repository JPA (En lugar de en memoria)**

```java
// src/main/java/.../repository/NotificationRepository.java
package com.minishop.notificationsservice.repository;

import com.minishop.notificationsservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Métodos automáticos por convención de nombres
    List<Notification> findByType(String type);
    List<Notification> findByReadFalse();  // No leídas
    List<Notification> findByTimestampAfter(LocalDateTime timestamp);
    
    // Queries personalizadas
    @Query("SELECT n FROM Notification n ORDER BY n.timestamp DESC")
    List<Notification> findAllOrderByTimestampDesc();
    
    @Query("SELECT n FROM Notification n WHERE n.timestamp >= :since ORDER BY n.timestamp DESC")
    List<Notification> findRecentNotifications(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.read = false")
    long countUnreadNotifications();
    
    // Top N más recientes
    List<Notification> findTop50ByOrderByTimestampDesc();
}
```

### **3. Service Actualizado**

```java
// Fragmento del NotificationService.java
@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;  // ← JPA Repository
    
    private void sendNotification(String message, String type) {
        // Crear notificación
        Notification notification = createNotificationByType(type, message);
        
        // Guardar en BD H2 (¡automáticamente!)
        notificationRepository.save(notification);
        
        // ... resto del código
    }
}
```

---

## 🔍 **Cómo Ver la Base de Datos H2**

### **1. Habilitar H2 Console**
```yaml
# application.yml
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    show-sql: true  # Ver SQL en logs
```

### **2. Acceder a la Consola**
1. Ejecutar aplicación: `./mvnw spring-boot:run`
2. Abrir: `http://localhost:8083/h2-console`
3. Configurar conexión:
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **User Name**: `sa`
   - **Password**: *(dejar vacío)*

### **3. Consultar Datos**
```sql
-- Ver todas las notificaciones
SELECT * FROM notifications ORDER BY created_at DESC;

-- Contar no leídas
SELECT COUNT(*) FROM notifications WHERE is_read = false;

-- Por tipo
SELECT type, COUNT(*) FROM notifications GROUP BY type;
```

---

## 📊 **Ventajas de H2 vs In-Memory Repository**

| Aspecto | Repository In-Memory | H2 + JPA |
|---------|---------------------|-----------|
| **Persistencia** | ❌ Se pierde al reiniciar | ✅ Persiste durante ejecución |
| **Consultas** | ❌ Lógica manual | ✅ SQL automático |
| **Relaciones** | ❌ Difícil de manejar | ✅ Foreign keys, joins |
| **Performance** | ✅ Muy rápido | ✅ Rápido + optimizado |
| **Escalabilidad** | ❌ Limitado por memoria | ✅ Mejor gestión |
| **Testing** | ❌ Lógica duplicada | ✅ Misma lógica que producción |

---

## 🔄 **Migración: In-Memory → H2 + JPA**

Si quieres migrar tu implementación actual:

### **Paso 1: Actualizar Entidad**
- Agregar anotaciones JPA: `@Entity`, `@Id`, `@Column`
- Cambiar `String id` por `Long id` con `@GeneratedValue`

### **Paso 2: Cambiar Repository**
- Extender `JpaRepository<Notification, Long>`
- Eliminar implementación manual
- Usar métodos automáticos de Spring Data

### **Paso 3: Actualizar Controller**
- Los endpoints siguen igual
- Cambiar `String id` por `Long id` en algunos métodos

### **Paso 4: Testing**
- Habilitar H2 console para ver datos
- Los datos persisten durante la ejecución
- Se resetean al reiniciar aplicación

---

## 🎯 **¿Cuándo Usar Cada Opción?**

### **🟡 In-Memory Repository (Tu implementación actual)**
- ✅ Prototipos rápidos
- ✅ Testing simple
- ✅ Casos donde no necesitas persistencia
- ✅ Lógica de negocio muy específica

### **🟢 H2 + JPA (Recomendado para desarrollo)**
- ✅ Aplicaciones más reales
- ✅ Cuando planeas migrar a PostgreSQL/MySQL
- ✅ Necesitas consultas complejas
- ✅ Relaciones entre entidades
- ✅ Mejor para testing de integración

### **🔵 PostgreSQL/MySQL (Producción)**
- ✅ Aplicaciones en producción
- ✅ Datos que deben persistir
- ✅ Múltiples usuarios concurrentes
- ✅ Backup y recuperación

---

## 🚀 **Próximos Pasos Recomendados**

1. **Mantén tu implementación actual** (funciona perfecto)
2. **Habilita H2 console** para explorar
3. **Cuando te sientas cómodo**, migra a JPA
4. **En el futuro**, cambia a PostgreSQL para producción

¡Tu implementación actual está **perfecta para aprender**! H2 + JPA es el siguiente nivel cuando quieras más features. 😊
