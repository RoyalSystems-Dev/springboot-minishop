#!/bin/bash
# Archivo: detect-ip.sh

# Función para detectar la mejor IP
detect_best_ip() {
    echo "🔍 Detectando IPs disponibles..."
    
    # Adaptado para Windows/WSL
    if command -v ip &> /dev/null; then
        # Linux/WSL
        DEFAULT_IP=$(ip route get 1.1.1.1 2>/dev/null | head -1 | awk '{print $7}')
        MAIN_IP=$(hostname -I | awk '{print $1}')
        PRIVATE_IPS=$(ip addr show | grep -E "inet (192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.)" | awk '{print $2}' | cut -d'/' -f1)
    else
        # Windows (usando PowerShell)
        DEFAULT_IP=$(powershell.exe -Command "Get-NetRoute -DestinationPrefix 0.0.0.0/0 | Get-NetIPAddress | Where-Object AddressFamily -eq IPv4 | Select-Object -First 1 -ExpandProperty IPAddress" 2>/dev/null)
        MAIN_IP=$(powershell.exe -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -notmatch '127\.|169\.254\.'} | Select-Object -First 1 -ExpandProperty IPAddress" 2>/dev/null)
        PRIVATE_IPS=$(powershell.exe -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -match '^(192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.)'} | Select-Object -ExpandProperty IPAddress" 2>/dev/null)
    fi
    
    echo "📊 IPs detectadas:"
    echo "  • IP por defecto: ${DEFAULT_IP:-'No detectada'}"
    echo "  • IP principal: ${MAIN_IP:-'No detectada'}"
    echo "  • IPs privadas disponibles:"
    
    local counter=1
    for ip in $PRIVATE_IPS; do
        echo "    $counter. $ip"
        ((counter++))
    done
    
    # Seleccionar la mejor IP
    if [ ! -z "$DEFAULT_IP" ] && [[ "$DEFAULT_IP" =~ ^(192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.) ]]; then
        SELECTED_IP=$DEFAULT_IP
    elif [ ! -z "$MAIN_IP" ] && [[ "$MAIN_IP" =~ ^(192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.) ]]; then
        SELECTED_IP=$MAIN_IP
    else
        SELECTED_IP=$(echo $PRIVATE_IPS | awk '{print $1}')
    fi
    
    echo ""
    echo "✅ IP recomendada: ${SELECTED_IP:-'No se pudo determinar'}"
    echo $SELECTED_IP
}

# Función para verificar estado del Swarm
check_swarm_status() {
    local swarm_state=$(docker info --format '{{.Swarm.LocalNodeState}}' 2>/dev/null)
    local is_manager=$(docker info --format '{{.Swarm.ControlAvailable}}' 2>/dev/null)
    
    case "$swarm_state" in
        "active")
            if [ "$is_manager" = "true" ]; then
                echo "MANAGER"
            else
                echo "WORKER"
            fi
            ;;
        "inactive")
            echo "INACTIVE"
            ;;
        "pending")
            echo "PENDING"
            ;;
        "error")
            echo "ERROR"
            ;;
        *)
            echo "UNKNOWN"
            ;;
    esac
}

# Función para mostrar tokens de union
show_join_tokens() {
    echo "🔗 Tokens para unir nodos al cluster:"
    echo ""
    echo "📋 Para agregar un WORKER:"
    echo "   Ve a la máquina worker y ejecuta:"
    docker swarm join-token worker
    echo ""
    echo "📋 Para agregar un MANAGER:"
    echo "   Ve a la máquina manager y ejecuta:"
    docker swarm join-token manager
    echo ""
    echo "⚠️  IMPORTANTE: Debes ejecutar estos comandos en las máquinas remotas,"
    echo "   NO desde esta máquina (10.0.1.14)"
}

# Función para validar IP
validate_ip() {
    local ip=$1
    local valid_pattern="^([0-9]{1,3}\.){3}[0-9]{1,3}$"
    
    if [[ $ip =~ $valid_pattern ]]; then
        # Verificar que cada octeto sea <= 255
        IFS='.' read -a octets <<< "$ip"
        for octet in "${octets[@]}"; do
            if [ $octet -gt 255 ]; then
                return 1
            fi
        done
        
        # Verificar conectividad básica
        if ping -c 1 -W 1 $ip &> /dev/null; then
            return 0
        else
            echo "⚠️  IP $ip no responde a ping"
            return 1
        fi
    else
        echo "❌ Formato de IP inválido: $ip"
        return 1
    fi
}

# Función para inicializar swarm con verificación
init_swarm_auto() {
    local swarm_status=$(check_swarm_status)
    
    echo "🔍 Verificando estado del Swarm..."
    
    if [ "$swarm_status" = "MANAGER" ]; then
        echo "✅ Swarm ya está inicializado y este nodo es MANAGER"
        echo "📍 Tu IP de manager: $(docker info --format '{{.Swarm.NodeAddr}}')"
        show_join_tokens
        return 0
    elif [ "$swarm_status" = "WORKER" ]; then
        echo "⚠️  Este nodo ya está en un Swarm como WORKER"
        echo "No puedes inicializar un nuevo Swarm desde un worker"
        return 1
    fi
    
    local ip=$(detect_best_ip)
    
    if [ -z "$ip" ]; then
        echo "❌ No se pudo detectar una IP válida"
        exit 1
    fi
    
    echo "🚀 Inicializando Swarm con IP: $ip"
    
    if validate_ip $ip; then
        if docker swarm init --advertise-addr $ip; then
            echo "✅ Swarm inicializado exitosamente"
            echo "📍 Tu máquina ($ip) es ahora el nodo MANAGER"
            echo ""
            show_join_tokens
        else
            echo "❌ Error al inicializar Swarm"
            exit 1
        fi
    else
        echo "❌ Error: IP inválida o no disponible"
        exit 1
    fi
}

# Función para gestionar nodos remotos
manage_remote_nodes() {
    local swarm_status=$(check_swarm_status)
    
    if [ "$swarm_status" != "MANAGER" ]; then
        echo "❌ Esta función solo está disponible desde un nodo MANAGER"
        return 1
    fi
    
    echo "🌐 Gestión de Nodos Remotos"
    echo "=========================="
    echo ""
    echo "📋 Nodos actuales en el cluster:"
    docker node ls
    echo ""
    echo "Opciones:"
    echo "  1. 🔗 Mostrar tokens de unión"
    echo "  2. 📊 Verificar conectividad de nodos"
    echo "  3. 🚀 Generar script para nodo worker"
    echo "  4. 🔄 Rotar tokens de seguridad"
    echo "  5. 🗑️  Remover nodo del cluster"
    echo "  0. Volver"
    echo ""
    
    read -p "Selecciona una opción: " choice
    
    case "$choice" in
        1)
            show_join_tokens
            ;;
        2)
            echo "🔍 Verificando conectividad..."
            docker node ls --format "table {{.Hostname}}\t{{.Status}}\t{{.Availability}}\t{{.ManagerStatus}}"
            ;;
        3)
            generate_worker_script
            ;;
        4)
            echo "🔄 Rotando tokens..."
            docker swarm join-token --rotate worker
            docker swarm join-token --rotate manager
            echo "✅ Tokens rotados exitosamente"
            ;;
        5)
            echo "📋 Nodos disponibles para remover:"
            docker node ls --filter "role=worker"
            read -p "Introduce el ID o nombre del nodo: " node_id
            if [ ! -z "$node_id" ]; then
                docker node rm $node_id --force
                echo "✅ Nodo removido"
            fi
            ;;
        0)
            return 0
            ;;
        *)
            echo "❌ Opción inválida"
            ;;
    esac
}

# Función para generar script de worker
generate_worker_script() {
    local manager_ip=$(docker info --format '{{.Swarm.NodeAddr}}')
    local worker_token=$(docker swarm join-token worker -q)
    
    echo "📝 Generando script para nodo worker..."
    
    cat > join-worker.sh << EOF
#!/bin/bash
# Script para unir nodo worker al cluster
# Ejecutar en la máquina worker

echo "🔍 Verificando Docker..."
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado"
    exit 1
fi

echo "🔍 Verificando conectividad con manager..."
if ! ping -c 1 -W 5 $manager_ip &> /dev/null; then
    echo "❌ No se puede conectar con el manager ($manager_ip)"
    exit 1
fi

echo "🚀 Uniéndose al cluster..."
docker swarm join --token $worker_token $manager_ip:2377

if [ \$? -eq 0 ]; then
    echo "✅ Nodo worker unido exitosamente al cluster"
    echo "📍 Manager: $manager_ip"
else
    echo "❌ Error al unirse al cluster"
    exit 1
fi
EOF

    chmod +x join-worker.sh
    echo "✅ Script generado: join-worker.sh"
    echo ""
    echo "📋 Para usar:"
    echo "  1. Copia join-worker.sh a la máquina worker"
    echo "  2. Ejecuta: ./join-worker.sh"
}

# Función interactiva para seleccionar IP
select_ip_interactive() {
    echo "🔍 Selecciona la IP para Docker Swarm:"
    echo ""
    
    # Detectar IPs disponibles
    local detected_ip=$(detect_best_ip)
    local ips=()
    
    if command -v ip &> /dev/null; then
        ips=($(ip addr show | grep -E "inet (192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.)" | awk '{print $2}' | cut -d'/' -f1))
    else
        ips=($(powershell.exe -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -match '^(192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.)'} | Select-Object -ExpandProperty IPAddress"))
    fi
    
    if [ ${#ips[@]} -eq 0 ]; then
        echo "❌ No se encontraron IPs privadas válidas"
        exit 1
    fi
    
    for i in "${!ips[@]}"; do
        echo "  $((i+1)). ${ips[$i]}"
    done
    
    echo "  0. Introducir IP manualmente"
    echo ""
    
    read -p "Selecciona una opción: " choice
    
    if [ "$choice" = "0" ]; then
        read -p "Introduce la IP: " custom_ip
        if validate_ip $custom_ip; then
            SELECTED_IP=$custom_ip
        else
            echo "❌ IP inválida"
            exit 1
        fi
    elif [ "$choice" -ge 1 ] && [ "$choice" -le ${#ips[@]} ]; then
        SELECTED_IP=${ips[$((choice-1))]}
    else
        echo "❌ Opción inválida"
        exit 1
    fi
    
    echo "✅ IP seleccionada: $SELECTED_IP"
    
    # Confirmar inicialización
    read -p "¿Inicializar Swarm con esta IP? (y/n): " confirm
    if [[ "$confirm" =~ ^[Yy]$ ]]; then
        docker swarm init --advertise-addr $SELECTED_IP
        echo "✅ Swarm inicializado exitosamente"
        show_join_tokens
    fi
}

# Función para configurar DinD managers
setup_dind_managers() {
    echo "🐳 Configuración de Managers DinD"
    echo "================================="
    echo ""
    
    echo "⚠️  ADVERTENCIA: DinD es solo para desarrollo/testing"
    echo "     No usar en producción"
    echo ""
    
    echo "¿Cuántos managers DinD quieres crear?"
    echo "  1. 1 Manager (sin tolerancia a fallos)"
    echo "  2. 3 Managers (tolerancia: 1 fallo)"
    echo "  3. 5 Managers (tolerancia: 2 fallos)"
    echo ""
    
    read -p "Selecciona (1-3): " manager_choice
    
    case $manager_choice in
        1) MANAGERS_COUNT=1 ;;
        2) MANAGERS_COUNT=3 ;;
        3) MANAGERS_COUNT=5 ;;
        *) echo "Opción inválida"; return 1 ;;
    esac
    
    read -p "¿Cuántos workers DinD? (0-5): " workers_count
    
    if [ "$workers_count" -lt 0 ] || [ "$workers_count" -gt 5 ]; then
        echo "❌ Número inválido de workers"
        return 1
    fi
    
    echo ""
    echo "📊 Configuración seleccionada:"
    echo "  • Managers: $MANAGERS_COUNT"
    echo "  • Workers: $workers_count"
    echo "  • Total nodos: $((MANAGERS_COUNT + workers_count))"
    echo ""
    
    read -p "¿Continuar? (y/n): " confirm
    if [[ "$confirm" =~ ^[Yy]$ ]]; then
        # Crear script de configuración
        cat > setup-dind-cluster.sh << 'EOF'
#!/bin/bash
# Script generado automáticamente para configurar DinD cluster

MANAGERS_COUNT=__MANAGERS_COUNT__
WORKERS_COUNT=__WORKERS_COUNT__
NETWORK_NAME="swarm-dind-network"

# Función para crear red
create_network() {
    echo "🌐 Creando red DinD..."
    docker network create \
        --driver bridge \
        --subnet=172.20.0.0/16 \
        $NETWORK_NAME 2>/dev/null || true
}

# Función para crear managers
create_managers() {
    echo "👥 Creando $MANAGERS_COUNT managers..."
    
    for i in $(seq 1 $MANAGERS_COUNT); do
        echo "  Creando dind-manager-$i..."
        
        docker run -d \
            --name dind-manager-$i \
            --hostname dind-manager-$i \
            --network $NETWORK_NAME \
            --ip 172.20.0.$((9 + i)) \
            --privileged \
            --publish $((2377 + i - 1)):2377 \
            --volume /var/lib/docker \
            --restart unless-stopped \
            docker:24-dind
        
        sleep 5
    done
}

# Función para crear workers
create_workers() {
    if [ $WORKERS_COUNT -eq 0 ]; then
        return 0
    fi
    
    echo "🔨 Creando $WORKERS_COUNT workers..."
    
    for i in $(seq 1 $WORKERS_COUNT); do
        echo "  Creando dind-worker-$i..."
        
        docker run -d \
            --name dind-worker-$i \
            --hostname dind-worker-$i \
            --network $NETWORK_NAME \
            --ip 172.20.0.$((20 + i)) \
            --privileged \
            --publish $((8080 + i)):8080 \
            --volume /var/lib/docker \
            --restart unless-stopped \
            docker:24-dind
        
        sleep 5
    done
}

# Función para inicializar swarm
init_swarm() {
    echo "🚀 Inicializando Swarm..."
    
    # Inicializar en primer manager
    docker exec dind-manager-1 docker swarm init --advertise-addr 172.20.0.10
    
    # Obtener tokens
    MANAGER_TOKEN=$(docker exec dind-manager-1 docker swarm join-token manager -q)
    WORKER_TOKEN=$(docker exec dind-manager-1 docker swarm join-token worker -q)
    
    # Unir managers adicionales
    for i in $(seq 2 $MANAGERS_COUNT); do
        echo "  Uniendo dind-manager-$i..."
        docker exec dind-manager-$i docker swarm join \
            --token $MANAGER_TOKEN \
            172.20.0.10:2377
    done
    
    # Unir workers
    for i in $(seq 1 $WORKERS_COUNT); do
        echo "  Uniendo dind-worker-$i..."
        docker exec dind-worker-$i docker swarm join \
            --token $WORKER_TOKEN \
            172.20.0.10:2377
    done
}

# Función principal
main() {
    echo "🐳 Configurando cluster DinD..."
    
    create_network
    create_managers
    create_workers
    
    echo "⏳ Esperando que Docker daemons estén listos..."
    sleep 20
    
    init_swarm
    
    echo ""
    echo "✅ Cluster DinD configurado!"
    echo ""
    echo "📊 Estado del cluster:"
    docker exec dind-manager-1 docker node ls
    
    echo ""
    echo "🔗 Accesos:"
    for i in $(seq 1 $MANAGERS_COUNT); do
        echo "  • Manager $i: localhost:$((2377 + i - 1))"
    done
}

# Ejecutar
main
EOF

        # Reemplazar variables
        sed -i "s/__MANAGERS_COUNT__/$MANAGERS_COUNT/g" setup-dind-cluster.sh
        sed -i "s/__WORKERS_COUNT__/$workers_count/g" setup-dind-cluster.sh
        
        chmod +x setup-dind-cluster.sh
        
        echo "✅ Script generado: setup-dind-cluster.sh"
        echo ""
        read -p "¿Ejecutar ahora? (y/n): " run_now
        if [[ "$run_now" =~ ^[Yy]$ ]]; then
            ./setup-dind-cluster.sh
        fi
    fi
}

# Menú principal
main() {
    case "${1:-auto}" in
        "auto")
            init_swarm_auto
            ;;
        "detect")
            detect_best_ip
            ;;
        "interactive")
            select_ip_interactive
            ;;
        "validate")
            if [ -z "$2" ]; then
                echo "Uso: $0 validate <ip>"
                exit 1
            fi
            validate_ip $2
            ;;
        "tokens")
            show_join_tokens
            ;;
        "nodes")
            manage_remote_nodes
            ;;
        "dind")
            setup_dind_managers
            ;;
        "status")
            local status=$(check_swarm_status)
            echo "Estado del Swarm: $status"
            if [ "$status" = "MANAGER" ]; then
                docker node ls
            fi
            ;;
        *)
            echo "Uso: $0 [auto|detect|interactive|validate|tokens|nodes|dind|status]"
            echo ""
            echo "Opciones:"
            echo "  auto        - Detectar y usar automáticamente"
            echo "  detect      - Solo mostrar IPs detectadas"
            echo "  interactive - Seleccionar IP manualmente"
            echo "  validate    - Validar una IP específica"
            echo "  tokens      - Mostrar tokens de unión"
            echo "  nodes       - Gestionar nodos remotos"
            echo "  dind        - Configurar managers DinD (desarrollo)"
            echo "  status      - Mostrar estado del Swarm"
            exit 1
            ;;
    esac
}

# Ejecutar
main "$@"