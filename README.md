# Mini Shop

Demo de Mini Shop.

## curl

```sh
# Obtener todos los productos
curl http://localhost:8082/products

# Obtener un producto específico
curl http://localhost:8082/products/1

# Crear un nuevo producto
curl -X POST http://localhost:8082/products -H "Content-Type: application/json" -d '{"name":"New Product","price":250.0}'

# Actualizar un producto
curl -X PUT http://localhost:8082/products/1 -H "Content-Type: application/json" -d '{"name":"Updated Product","price":150.0}'

# Eliminar un producto
curl -X DELETE http://localhost:8082/products/1
```