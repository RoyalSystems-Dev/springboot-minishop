# Mini Shop

Demo de Mini Shop.


## curl products

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


## curl orders

```sh
# Obtener todas las órdenes
curl http://localhost:8081/orders

# Obtener orden específica
curl http://localhost:8081/orders/1

# Crear nueva orden
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d '{
  "productName": "Laptop",
  "quantity": 2
}'

# Actualizar orden
curl -X PUT http://localhost:8081/orders/1 -H "Content-Type: application/json" -d '{
  "productName": "Updated Product",
  "quantity": 5
}'

# Buscar órdenes por producto
curl http://localhost:8081/orders/product/Product-1

# Eliminar orden
curl -X DELETE http://localhost:8081/orders/1
```