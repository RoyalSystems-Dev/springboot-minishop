#!/bin/bash
# Script para crear múltiples managers usando Docker-in-Docker

# Configuración
NETWORK_NAME="swarm-dind-network"
MANAGERS_COUNT=3
WORKERS_COUNT=2

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_step() {
    echo -e "${GREEN}➤ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Función para crear red personalizada
create_network() {
    print_step "Creando red para DinD managers..."
    
    # Verificar si la red ya existe
    if docker network ls | grep -q "$NETWORK_NAME"; then
        print_warning "Red $NETWORK_NAME ya existe"
        return 0
    fi
    
    # Crear red bridge personalizada
    docker network create \
        --driver bridge \
        --subnet=172.20.0.0/16 \
        --ip-range=172.20.240.0/20 \
        $NETWORK_NAME
    
    print_info "Red $NETWORK_NAME creada exitosamente"
}

# Función para crear contenedor DinD
create_dind_container() {
    local container_name=$1
    local port_offset=$2
    local ip_suffix=$3
    
    print_step "Creando contenedor DinD: $container_name"
    
    # Detener y remover contenedor si existe
    docker stop $container_name 2>/dev/null || true
    docker rm $container_name 2>/dev/null || true
    
    # Crear contenedor DinD
    docker run -d \
        --name $container_name \
        --hostname $container_name \
        --network $NETWORK_NAME \
        --ip 172.20.0.$ip_suffix \
        --privileged \
        --publish $((2377 + port_offset)):2377 \
        --publish $((8080 + port_offset)):8080 \
        --volume /var/lib/docker \
        --restart unless-stopped \
        docker:24-dind
    
    # Esperar a que Docker daemon esté listo
    print_info "Esperando que Docker daemon esté listo en $container_name..."
    sleep 10
    
    # Verificar que Docker funciona
    if docker exec $container_name docker info &>/dev/null; then
        print_info "✅ Docker daemon listo en $container_name"
        return 0
    else
        print_error "❌ Error: Docker daemon no está listo en $container_name"
        return 1
    fi
}

# Función para inicializar swarm en el primer manager
init_swarm_leader() {
    local leader_container="dind-manager-1"
    local leader_ip="172.20.0.10"
    
    print_step "Inicializando Swarm en el contenedor líder..."
    
    # Inicializar swarm
    docker exec $leader_container docker swarm init --advertise-addr $leader_ip
    
    if [ $? -eq 0 ]; then
        print_info "✅ Swarm inicializado en $leader_container"
        
        # Obtener tokens
        MANAGER_TOKEN=$(docker exec $leader_container docker swarm join-token manager -q)
        WORKER_TOKEN=$(docker exec $leader_container docker swarm join-token worker -q)
        
        print_info "Manager token: $MANAGER_TOKEN"
        print_info "Worker token: $WORKER_TOKEN"
        
        return 0
    else
        print_error "❌ Error al inicializar Swarm"
        return 1
    fi
}

# Función para unir managers adicionales
join_additional_managers() {
    local leader_ip="172.20.0.10"
    
    print_step "Uniendo managers adicionales al swarm..."
    
    for i in $(seq 2 $MANAGERS_COUNT); do
        local container_name="dind-manager-$i"
        local manager_ip="172.20.0.$((9 + i))"
        
        print_info "Uniendo $container_name al swarm..."
        
        docker exec $container_name docker swarm join \
            --token $MANAGER_TOKEN \
            $leader_ip:2377
        
        if [ $? -eq 0 ]; then
            print_info "✅ $container_name unido como manager"
        else
            print_error "❌ Error al unir $container_name"
        fi
    done
}

# Función para crear workers
create_and_join_workers() {
    local leader_ip="172.20.0.10"
    
    print_step "Creando y uniendo workers..."
    
    for i in $(seq 1 $WORKERS_COUNT); do
        local container_name="dind-worker-$i"
        local worker_ip="172.20.0.$((20 + i))"
        local port_offset=$((100 + i))
        
        # Crear contenedor worker
        create_dind_container $container_name $port_offset $((20 + i))
        
        # Unir al swarm como worker
        print_info "Uniendo $container_name al swarm como worker..."
        
        docker exec $container_name docker swarm join \
            --token $WORKER_TOKEN \
            $leader_ip:2377
        
        if [ $? -eq 0 ]; then
            print_info "✅ $container_name unido como worker"
        else
            print_error "❌ Error al unir $container_name"
        fi
    done
}

# Función para mostrar estado del cluster
show_cluster_status() {
    print_step "Estado del cluster DinD:"
    echo ""
    
    # Mostrar nodos desde el líder
    docker exec dind-manager-1 docker node ls
    
    echo ""
    print_info "Contenedores DinD activos:"
    docker ps --filter "name=dind-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    echo ""
    print_info "Acceso a los managers:"
    for i in $(seq 1 $MANAGERS_COUNT); do
        local port=$((2377 + i - 1))
        echo "  • Manager $i: localhost:$port"
    done
}

# Función para limpiar entorno
cleanup_environment() {
    print_step "Limpiando entorno DinD..."
    
    # Detener y remover contenedores
    for i in $(seq 1 $MANAGERS_COUNT); do
        docker stop dind-manager-$i 2>/dev/null || true
        docker rm dind-manager-$i 2>/dev/null || true
    done
    
    for i in $(seq 1 $WORKERS_COUNT); do
        docker stop dind-worker-$i 2>/dev/null || true
        docker rm dind-worker-$i 2>/dev/null || true
    done
    
    # Remover red
    docker network rm $NETWORK_NAME 2>/dev/null || true
    
    print_info "✅ Entorno limpiado"
}

# Función para desplegar servicios de prueba
deploy_test_services() {
    print_step "Desplegando servicios de prueba..."
    
    # Crear servicio de prueba
    docker exec dind-manager-1 docker service create \
        --name test-nginx \
        --replicas 5 \
        --publish 8080:80 \
        nginx:alpine
    
    print_info "✅ Servicio test-nginx desplegado"
    
    # Mostrar servicios
    docker exec dind-manager-1 docker service ls
    docker exec dind-manager-1 docker service ps test-nginx
}

# Función para escalar servicios
scale_services() {
    print_step "Escalando servicios..."
    
    read -p "¿Cuántas réplicas para test-nginx?: " replicas
    
    docker exec dind-manager-1 docker service scale test-nginx=$replicas
    
    print_info "✅ Servicio escalado a $replicas réplicas"
    docker exec dind-manager-1 docker service ps test-nginx
}

# Función para crear configuración completa
setup_full_cluster() {
    print_step "Configurando cluster DinD completo..."
    
    # Limpiar entorno anterior
    cleanup_environment
    
    # Crear red
    create_network
    
    # Crear managers
    for i in $(seq 1 $MANAGERS_COUNT); do
        create_dind_container "dind-manager-$i" $((i-1)) $((9 + i))
    done
    
    # Inicializar swarm
    init_swarm_leader
    
    # Unir managers adicionales
    if [ $MANAGERS_COUNT -gt 1 ]; then
        join_additional_managers
    fi
    
    # Crear workers
    if [ $WORKERS_COUNT -gt 0 ]; then
        create_and_join_workers
    fi
    
    # Mostrar estado
    show_cluster_status
    
    print_info "🎉 Cluster DinD configurado exitosamente!"
}

# Función para monitorear cluster
monitor_cluster() {
    print_step "Monitoreando cluster DinD..."
    
    while true; do
        clear
        echo "=== Monitor de Cluster DinD ==="
        echo "Actualizado: $(date)"
        echo ""
        
        # Estado de contenedores
        echo "📦 Contenedores DinD:"
        docker ps --filter "name=dind-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        echo ""
        
        # Estado de nodos
        echo "🔗 Nodos del Swarm:"
        docker exec dind-manager-1 docker node ls 2>/dev/null || echo "Error: No se puede conectar al swarm"
        echo ""
        
        # Estado de servicios
        echo "🚀 Servicios activos:"
        docker exec dind-manager-1 docker service ls 2>/dev/null || echo "No hay servicios desplegados"
        echo ""
        
        echo "Presiona Ctrl+C para salir"
        sleep 5
    done
}

# Menú principal
main() {
    case "${1:-setup}" in
        "setup")
            setup_full_cluster
            ;;
        "status")
            show_cluster_status
            ;;
        "deploy")
            deploy_test_services
            ;;
        "scale")
            scale_services
            ;;
        "monitor")
            monitor_cluster
            ;;
        "cleanup")
            cleanup_environment
            ;;
        *)
            echo "Uso: $0 [setup|status|deploy|scale|monitor|cleanup]"
            echo ""
            echo "Opciones:"
            echo "  setup   - Configurar cluster DinD completo"
            echo "  status  - Mostrar estado del cluster"
            echo "  deploy  - Desplegar servicios de prueba"
            echo "  scale   - Escalar servicios"
            echo "  monitor - Monitorear cluster en tiempo real"
            echo "  cleanup - Limpiar entorno"
            exit 1
            ;;
    esac
}

# Ejecutar
main "$@"