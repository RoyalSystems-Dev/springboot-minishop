# 🚀 Guía de Desarrollo - Mini Shop Interfaces

Esta guía explica cómo trabajar con las interfaces web configuradas para desarrollo en tiempo real sin necesidad de compilar.

## 📁 Estructura de Directorios

```
mini-shop/
├── orders-service/
│   ├── public/                     # 🔥 Desarrollo en caliente
│   │   ├── index.html
│   │   ├── css/app.css
│   │   └── js/
│   │       ├── main.js
│   │       └── App.js
│   └── src/main/resources/static/ # 📦 Producción (compilado)
│
└── products-service/
    ├── public/                     # 🔥 Desarrollo en caliente  
    │   ├── index.html
    │   ├── css/app.css
    │   └── js/
    │       ├── main.js
    │       └── App.js
    └── src/main/resources/static/ # 📦 Producción (compilado)
```

## ⚡ Desarrollo en Tiempo Real

### Configuración Activa
Ambos servicios están configurados para servir archivos directamente desde `public/`:

**Orders Service (Puerto 8081):**
```yaml
spring:
  web:
    resources:
      static-locations:
      - file:${user.dir}/public/        # 🔥 Desarrollo sin compilar
      - classpath:/static/              # 📦 Fallback producción
      - classpath:/public/              # 📦 Fallback adicional
```

**Products Service (Puerto 8082):**
```yaml
spring:
  web:
    resources:
      static-locations:
      - file:${user.dir}/public/        # 🔥 Desarrollo sin compilar
      - classpath:/static/              # 📦 Fallback producción
      - classpath:/public/              # 📦 Fallback adicional
```

## 🔄 Flujo de Desarrollo

### 1. Iniciar Servicios
```bash
# Terminal 1 - Orders Service
cd orders-service
./mvnw spring-boot:run

# Terminal 2 - Products Service  
cd products-service
./mvnw spring-boot:run
```

### 2. Desarrollo en Caliente
✅ **Editar archivos en `public/`** - Cambios inmediatos
- `public/index.html` - HTML principal
- `public/css/app.css` - Estilos
- `public/js/App.js` - Lógica Vue.js
- `public/js/main.js` - Inicialización

❌ **No editar en `src/main/resources/static/`** - Requiere compilación

### 3. Ver Cambios Instantáneos
- **Orders**: http://localhost:8081 🔄 F5 para ver cambios
- **Products**: http://localhost:8082 🔄 F5 para ver cambios

## 🎯 Ventajas de esta Configuración

### ✅ Desarrollo Rápido
- **Sin compilación** - Cambios inmediatos
- **Recarga simple** - Solo F5 en navegador
- **Debugging fácil** - Archivos fuente directos
- **Iteración rápida** - Probar cambios al instante

### ✅ Flexibilidad
- **Desarrollo**: Usa archivos de `public/`
- **Producción**: Usa archivos compilados de `src/main/resources/static/`
- **Fallback automático** - Si no encuentra en `public/`, busca en `static/`

## 📝 Workflows Recomendados

### Para Cambios de Interfaz
1. **Editar directamente en `public/`**
2. **Guardar archivo**
3. **Refrescar navegador (F5)**
4. **Ver cambios inmediatamente**

### Para Deployment
1. **Copiar archivos finales a `src/main/resources/static/`**
2. **Compilar proyecto**: `./mvnw clean package`
3. **Deployar JAR con interfaces integradas**

## 🎨 Customización por Servicio

### Orders Service (Azul/Púrpura)
```css
/* public/css/app.css */
.header {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}
```

### Products Service (Verde)
```css
/* public/css/app.css */
.header {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}
```

## 🔧 Comandos Útiles

### Desarrollo
```bash
# Iniciar orders service
cd orders-service && ./mvnw spring-boot:run

# Iniciar products service  
cd products-service && ./mvnw spring-boot:run

# Ver logs en tiempo real
tail -f logs/application.log
```

### Sincronización
```bash
# Copiar cambios de public/ a static/ (cuando estés listo)
cp -r orders-service/public/* orders-service/src/main/resources/static/
cp -r products-service/public/* products-service/src/main/resources/static/
```

### Testing
```bash
# Probar endpoints API
curl http://localhost:8081/orders
curl http://localhost:8082/products

# Verificar interfaces
curl http://localhost:8081/
curl http://localhost:8082/
```

## 🐛 Troubleshooting

### Interface no se actualiza
1. **Verificar ruta**: ¿Estás editando en `public/`?
2. **Limpiar cache**: Ctrl+Shift+R (hard refresh)
3. **Verificar logs**: ¿Hay errores en consola?
4. **Verificar configuración**: ¿application.yml correcto?

### Archivos no se encuentran
1. **Verificar working directory**: ¿Spring Boot se ejecuta desde la raíz correcta?
2. **Verificar permisos**: ¿Los archivos son legibles?
3. **Verificar estructura**: ¿Existe el directorio `public/`?

### Conflictos de archivos
1. **Prioridad**: `file:${user.dir}/public/` tiene prioridad
2. **Fallback**: Si no encuentra en `public/`, busca en `classpath:`
3. **Debug**: Activar logs de Spring para ver resolución de recursos

## 📊 Puertos y URLs

| Servicio | Puerto | URL Interface | URL API |
|----------|--------|---------------|---------|
| Orders | 8081 | http://localhost:8081 | http://localhost:8081/orders |
| Products | 8082 | http://localhost:8082 | http://localhost:8082/products |

## 🎉 ¡Desarrollo Eficiente!

Con esta configuración puedes:
- ✨ **Desarrollar interfaces rápidamente**
- 🔄 **Ver cambios al instante**
- 🎨 **Experimentar con estilos**
- 🧪 **Probar funcionalidades**
- 🚀 **Iterar sin fricción**

---

**¡Happy Coding! 💻✨**
