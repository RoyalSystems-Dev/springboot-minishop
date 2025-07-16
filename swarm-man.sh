#!/bin/bash

STACK_NAME="mini-shop"
COMPOSE_FILE="docker-compose.swarm.yml"

# Funciones de utilidad
print_step() {
    echo -e "\033[0;32m➤ $1\033[0m"
}

print_info() {
    echo -e "\033[0;34mℹ️  $1\033[0m"
}

# Función para mostrar distribución actual
show_distribution() {
    print_step "Distribución actual de servicios:"
    echo ""
    
    # Servicios del stack
    echo "📊 Servicios en el stack:"
    docker stack services $STACK_NAME
    echo ""
    
    # Distribución por nodo
    echo "📋 Distribución por nodo:"
    for node in $(docker node ls --format "{{.Hostname}}"); do
        echo "🖥️  Nodo: $node"
        docker node ps $node --format "table {{.Name}}\t{{.Image}}\t{{.CurrentState}}\t{{.DesiredState}}"
        echo ""
    done
}

# Función para rebalancear servicios
rebalance_services() {
    print_step "Rebalanceando servicios..."
    
    # Forzar redistribución
    docker service update --force $STACK_NAME_orders-service
    docker service update --force $STACK_NAME_products-service
    docker service update --force $STACK_NAME_notifications-service
    
    print_info "✅ Rebalanceo iniciado"
}

# Función para escalar servicios
scale_services() {
    echo "¿Qué servicio quieres escalar?"
    echo "1. orders-service (actual: $(docker service ls --filter name=$STACK_NAME_orders-service --format '{{.Replicas}}'))"
    echo "2. products-service (actual: $(docker service ls --filter name=$STACK_NAME_products-service --format '{{.Replicas}}'))"
    echo "3. notifications-service (actual: $(docker service ls --filter name=$STACK_NAME_notifications-service --format '{{.Replicas}}'))"
    echo "4. Escalar todos automáticamente"
    echo ""
    
    read -p "Selecciona (1-4): " service_choice
    
    case $service_choice in
        1)
            read -p "¿Cuántas réplicas para orders-service?: " replicas
            docker service scale $STACK_NAME_orders-service=$replicas
            ;;
        2)
            read -p "¿Cuántas réplicas para products-service?: " replicas
            docker service scale $STACK_NAME_products-service=$replicas
            ;;
        3)
            read -p "¿Cuántas réplicas para notifications-service?: " replicas
            docker service scale $STACK_NAME_notifications-service=$replicas
            ;;
        4)
            auto_scale_services
            ;;
        *)
            echo "Opción inválida"
            ;;
    esac
}

# Función para escalado automático basado en número de nodos
auto_scale_services() {
    local workers_count=$(docker node ls --filter "role=worker" --format "{{.ID}}" | wc -l)
    
    print_step "Escalando automáticamente basado en $workers_count workers..."
    
    if [ $workers_count -ge 4 ]; then
        docker service scale $STACK_NAME_orders-service=6
        docker service scale $STACK_NAME_products-service=4
        docker service scale $STACK_NAME_notifications-service=2
    elif [ $workers_count -ge 2 ]; then
        docker service scale $STACK_NAME_orders-service=4
        docker service scale $STACK_NAME_products-service=3
        docker service scale $STACK_NAME_notifications-service=2
    else
        docker service scale $STACK_NAME_orders-service=2
        docker service scale $STACK_NAME_products-service=2
        docker service scale $STACK_NAME_notifications-service=1
    fi
    
    print_info "✅ Escalado automático completado"
}

# Función para aplicar constraints específicos
apply_constraints() {
    echo "🎯 Aplicar constraints específicos:"
    echo "1. Mover NATS solo a managers"
    echo "2. Mover APIs solo a workers"
    echo "3. Aplicar distribución por zona"
    echo "4. Reset constraints"
    echo ""
    
    read -p "Selecciona (1-4): " constraint_choice
    
    case $constraint_choice in
        1)
            docker service update --constraint-add "node.role==manager" $STACK_NAME_nats-server
            ;;
        2)
            docker service update --constraint-add "node.role==worker" $STACK_NAME_orders-service
            docker service update --constraint-add "node.role==worker" $STACK_NAME_products-service
            docker service update --constraint-add "node.role==worker" $STACK_NAME_notifications-service
            ;;
        3)
            echo "Etiqueta tus nodos primero:"
            echo "docker node update --label-add zone=east worker1"
            echo "docker node update --label-add zone=west worker2"
            ;;
        4)
            docker service update --constraint-rm "node.role==manager" $STACK_NAME_nats-server
            docker service update --constraint-rm "node.role==worker" $STACK_NAME_orders-service
            ;;
        *)
            echo "Opción inválida"
            ;;
    esac
}

# Menú principal
main() {
    case "${1:-menu}" in
        "deploy")
            print_step "Desplegando stack..."
            docker stack deploy -c $COMPOSE_FILE $STACK_NAME
            ;;
        "status")
            show_distribution
            ;;
        "scale")
            scale_services
            ;;
        "rebalance")
            rebalance_services
            ;;
        "constraints")
            apply_constraints
            ;;
        "remove")
            print_step "Removiendo stack..."
            docker stack rm $STACK_NAME
            ;;
        "menu"|*)
            echo "🐳 Gestión de Despliegue Swarm"
            echo "============================="
            echo ""
            echo "1. deploy      - Desplegar stack"
            echo "2. status      - Ver distribución actual"
            echo "3. scale       - Escalar servicios"
            echo "4. rebalance   - Rebalancear servicios"
            echo "5. constraints - Aplicar constraints"
            echo "6. remove      - Remover stack"
            echo ""
            echo "Uso: $0 [deploy|status|scale|rebalance|constraints|remove]"
            ;;
    esac
}

# Ejecutar
main "$@"