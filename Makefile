# Makefile para Mini-Shop
# Simplifica los comandos Docker más comunes

.PHONY: help build up down logs clean dev-up dev-down restart status health

# Variables
COMPOSE_FILE = docker-compose.yml
DEV_COMPOSE_FILE = docker-compose.dev.yml
PROJECT_NAME = mini-shop

# Colores para output
GREEN = \033[0;32m
BLUE = \033[0;34m
YELLOW = \033[1;33m
NC = \033[0m

help: ## Mostrar ayuda
	@echo "$(BLUE)🚀 Mini-Shop - Comandos disponibles:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-15s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(YELLOW)Ejemplos de uso:$(NC)"
	@echo "  make build    # Construir y desplegar"
	@echo "  make logs     # Ver logs en tiempo real"
	@echo "  make dev-up   # Solo infraestructura para desarrollo"

build: ## Construir servicios Java y crear imágenes Docker
	@echo "$(BLUE)📦 Construyendo servicios Java...$(NC)"
	mvn clean install -DskipTests
	cd orders-service && mvn clean package -DskipTests
	cd products-service && mvn clean package -DskipTests
	cd notifications-service && mvn clean package -DskipTests
	@echo "$(BLUE)🐳 Construyendo imágenes Docker...$(NC)"
	docker-compose build --no-cache

up: build ## Desplegar todos los servicios (producción)
	@echo "$(BLUE)🚀 Desplegando servicios...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)✅ Servicios desplegados!$(NC)"
	@make info

down: ## Detener todos los servicios
	@echo "$(BLUE)🛑 Deteniendo servicios...$(NC)"
	docker-compose down
	@echo "$(GREEN)✅ Servicios detenidos$(NC)"

logs: ## Ver logs de todos los servicios
	docker-compose logs -f

logs-orders: ## Ver logs del servicio de orders
	docker-compose logs -f orders-service

logs-products: ## Ver logs del servicio de products
	docker-compose logs -f products-service

logs-notifications: ## Ver logs del servicio de notifications
	docker-compose logs -f notifications-service

logs-nats: ## Ver logs de NATS
	docker-compose logs -f nats-server

dev-up: ## Levantar solo infraestructura para desarrollo
	@echo "$(BLUE)🔧 Levantando infraestructura de desarrollo...$(NC)"
	docker-compose -f $(DEV_COMPOSE_FILE) up -d
	@echo "$(GREEN)✅ Infraestructura lista para desarrollo!$(NC)"
	@echo ""
	@echo "$(YELLOW)Ahora puedes ejecutar los servicios localmente:$(NC)"
	@echo "  cd orders-service && ./mvnw spring-boot:run"
	@echo "  cd products-service && ./mvnw spring-boot:run"
	@echo "  cd notifications-service && ./mvnw spring-boot:run"

dev-down: ## Detener infraestructura de desarrollo
	docker-compose -f $(DEV_COMPOSE_FILE) down

clean: ## Limpiar contenedores, imágenes y volúmenes
	@echo "$(BLUE)🧹 Limpiando contenedores...$(NC)"
	docker-compose down --remove-orphans
	docker-compose -f $(DEV_COMPOSE_FILE) down --remove-orphans
	@echo "$(BLUE)🧹 Limpiando imágenes...$(NC)"
	docker image prune -f
	docker system prune -f
	@echo "$(GREEN)✅ Limpieza completada$(NC)"

restart: down up ## Reiniciar todos los servicios

status: ## Ver estado de los servicios
	@echo "$(BLUE)📊 Estado de los servicios:$(NC)"
	docker-compose ps

health: ## Verificar health checks de los servicios
	@echo "$(BLUE)❤️  Verificando health checks...$(NC)"
	@echo ""
	@echo "$(YELLOW)NATS:$(NC)"
	@curl -s http://localhost:8222/healthz && echo " ✅" || echo " ❌"
	@echo "$(YELLOW)Orders Service:$(NC)"
	@curl -s http://localhost:8081/actuator/health && echo " ✅" || echo " ❌"
	@echo "$(YELLOW)Products Service:$(NC)"
	@curl -s http://localhost:8082/actuator/health && echo " ✅" || echo " ❌"
	@echo "$(YELLOW)Notifications Service:$(NC)"
	@curl -s http://localhost:8083/actuator/health && echo " ✅" || echo " ❌"

info: ## Mostrar información de acceso a los servicios
	@echo ""
	@echo "$(GREEN)🌐 Servicios disponibles:$(NC)"
	@echo "  • Portal Principal:      http://localhost"
	@echo "  • Orders App:           http://localhost/orders-app"
	@echo "  • Products API:         http://localhost/products"
	@echo "  • Notifications App:    http://localhost/notifications-app"
	@echo "  • H2 Console:          http://localhost/h2-console"
	@echo "  • NATS Monitoring:     http://localhost:8222"
	@echo ""
	@echo "$(GREEN)🔍 Health Checks:$(NC)"
	@echo "  • General:             http://localhost/health"
	@echo "  • Orders:              http://localhost/health/orders"
	@echo "  • Products:            http://localhost/health/products"
	@echo "  • Notifications:       http://localhost/health/notifications"

shell-orders: ## Abrir shell en el contenedor de orders
	docker-compose exec orders-service /bin/sh

shell-products: ## Abrir shell en el contenedor de products
	docker-compose exec products-service /bin/sh

shell-notifications: ## Abrir shell en el contenedor de notifications
	docker-compose exec notifications-service /bin/sh

# Comandos de desarrollo rápido
quick-build: ## Build rápido sin tests
	mvn clean install -DskipTests -T 1C

quick-restart: ## Restart rápido de un servicio específico
	@echo "¿Qué servicio quieres reiniciar?"
	@echo "1. orders-service"
	@echo "2. products-service" 
	@echo "3. notifications-service"
	@read -p "Selecciona (1-3): " choice; \
	case $$choice in \
		1) docker-compose restart orders-service ;; \
		2) docker-compose restart products-service ;; \
		3) docker-compose restart notifications-service ;; \
		*) echo "Opción inválida" ;; \
	esac
