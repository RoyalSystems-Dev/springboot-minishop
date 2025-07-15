# En cada nodo del swarm, construye las imágenes:
docker build -t mini-shop/orders-service:latest ./orders-service
docker build -t mini-shop/products-service:latest ./products-service
docker build -t mini-shop/notifications-service:latest ./notifications-service