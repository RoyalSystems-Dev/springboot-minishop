# 🎯 Guía Práctica: H2 + JPA en Spring Boot

## 📚 **¿Qué es cada cosa?**

### **H2 Database**
- **Base de datos embebida** escrita en Java
- **Muy liviana** (solo ~2MB)
- **En memoria** o en archivo
- **Perfecta para desarrollo y pruebas**
- **Consola web incluida** para ver datos

### **JPA (Java Persistence API)**
- **Especificación** de Java para persistencia
- **Hibernate** es la implementación más usada
- **ORM** (Object-Relational Mapping)
- **Convierte objetos Java ↔ tablas de BD**

### **Spring Data JPA**
- **Capa sobre JPA** que simplifica todo
- **Métodos automáticos** por convención de nombres
- **Repositorios** sin escribir SQL

---

## 🔧 **Tu Configuración Actual**

### **1. Dependencias en `pom.xml`**
```xml
<!-- JPA + Hibernate -->
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

### **2. Configuración en `application.yml`**
```yaml
spring:
  # H2 Database
  datasource:
    url: jdbc:h2:mem:notificationsdb  # BD en memoria
    username: sa
    password: 
    driver-class-name: org.h2.Driver

  # Consola H2 (para ver la BD en el navegador)
  h2:
    console:
      enabled: true
      path: /h2-console

  # JPA/Hibernate
  jpa:
    show-sql: true              # Ver SQL en logs
    hibernate:
      ddl-auto: create-drop     # Crear tablas automáticamente
    properties:
      hibernate:
        format_sql: true        # SQL bonito en logs
```

---

## 🎯 **Cómo Funciona Todo Junto**

### **Paso 1: Entidad JPA**
```java
@Entity                    // Marca como tabla de BD
@Table(name = "notifications")
public class NotificationJPA {
    
    @Id                              // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento
    private Long id;
    
    @Column(nullable = false)        // Columna NOT NULL
    private String title;
    
    @Column(columnDefinition = "TEXT")  // Tipo específico
    private String message;
    
    // Constructor, getters, setters...
}
```

### **Paso 2: Repository**
```java
@Repository
public interface NotificationJPARepository extends JpaRepository<NotificationJPA, Long> {
    
    // ✨ MÉTODOS AUTOMÁTICOS por convención de nombres
    List<NotificationJPA> findByType(String type);
    List<NotificationJPA> findByReadFalse();
    List<NotificationJPA> findByTimestampAfter(LocalDateTime date);
    
    // ✨ CONSULTAS PERSONALIZADAS
    @Query("SELECT n FROM NotificationJPA n WHERE n.title LIKE %:keyword%")
    List<NotificationJPA> findByTitleContaining(@Param("keyword") String keyword);
}
```

### **Paso 3: Usar en el Controller**
```java
@RestController
public class NotificationController {
    
    @Autowired
    private NotificationJPARepository repository;
    
    @PostMapping("/notifications")
    public NotificationJPA create(@RequestBody NotificationJPA notification) {
        return repository.save(notification);  // INSERT automático
    }
    
    @GetMapping("/notifications")
    public List<NotificationJPA> getAll() {
        return repository.findAll();  // SELECT * automático
    }
    
    @GetMapping("/notifications/unread")
    public List<NotificationJPA> getUnread() {
        return repository.findByReadFalse();  // WHERE is_read = false
    }
}
```

---

## 🚀 **Ventajas de tu setup actual**

### **✅ H2 Database**
- ⚡ **Súper rápida** para desarrollo
- 🔧 **Fácil de configurar** (ya está listo)
- 👀 **Consola web** para ver datos
- 🧪 **Perfecta para pruebas**

### **✅ JPA + Spring Data**
- 📝 **Menos código** (métodos automáticos)
- 🛡️ **Seguro** (evita SQL injection)
- 🔄 **Flexible** (fácil cambiar de BD)
- 📊 **Relaciones** entre entidades

---

## 🎮 **¿Cómo empezar a usarlo?**

### **Opción 1: Probar la consola H2** 📊
1. Ejecuta tu aplicación: `./mvnw spring-boot:run`
2. Ve a: http://localhost:8083/h2-console
3. Configuración:
   - **JDBC URL**: `jdbc:h2:mem:notificationsdb`
   - **User Name**: `sa`
   - **Password**: (vacío)
4. Click "Connect"

### **Opción 2: Usar los endpoints JPA** 🔗
Ya tienes el `NotificationJPAController` configurado:

```bash
# Crear notificación
curl -X POST http://localhost:8083/api/jpa/notifications \
  -H "Content-Type: application/json" \
  -d '{"type":"test","title":"Prueba JPA","message":"Funciona!","severity":"INFO"}'

# Ver todas
curl http://localhost:8083/api/jpa/notifications

# Ver no leídas
curl http://localhost:8083/api/jpa/notifications/unread
```

### **Opción 3: Migrar tu código actual** 🔄
Cambiar tu `NotificationController` actual para usar JPA:

1. **Inyectar** `NotificationJPARepository`
2. **Cambiar** `List<Notification>` → `NotificationJPARepository`
3. **Usar** `repository.save()`, `repository.findAll()`, etc.

---

## 🔄 **Diferencias Clave**

| **Aspecto** | **Tu Código Actual** | **Con JPA + H2** |
|-------------|---------------------|-------------------|
| **Almacenamiento** | Memoria (se pierde) | Base de datos (persiste*) |
| **Métodos** | Manuales | Automáticos |
| **SQL** | No hay | Generado automáticamente |
| **Consultas** | Filtros manuales | Métodos por convención |
| **Escalabilidad** | Limitada | Fácil crecer |

*\*En tu config actual es memoria también, pero fácil cambiar a archivo*

---

## 🎯 **Recomendación**

**Para aprender y entender**, te sugiero:

1. **🔍 Explora la consola H2** primero
2. **🧪 Prueba los endpoints JPA** existentes  
3. **⚡ Ve los logs SQL** en la consola
4. **🔄 Migra gradualmente** tu código actual

¿Quieres que te ayude con algún paso específico?
