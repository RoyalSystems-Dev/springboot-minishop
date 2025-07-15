#!/bin/bash
# Archivo: detect-ip.sh

# Función para detectar la mejor IP
detect_best_ip() {
    echo "🔍 Detectando IPs disponibles..."
    
    # Método 1: IP de la ruta por defecto
    DEFAULT_IP=$(ip route get 1.1.1.1 2>/dev/null | head -1 | awk '{print $7}')
    
    # Método 2: IP de la interfaz principal
    MAIN_IP=$(hostname -I | awk '{print $1}')
    
    # Método 3: Todas las IPs privadas
    PRIVATE_IPS=$(ip addr show | grep -E "inet (192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.)" | awk '{print $2}' | cut -d'/' -f1)
    
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
        
        # Verificar que la IP esté asignada a una interfaz
        if ip addr show | grep -q "inet $ip/"; then
            return 0
        else
            echo "⚠️  IP $ip no está asignada a ninguna interfaz"
            return 1
        fi
    else
        echo "❌ Formato de IP inválido: $ip"
        return 1
    fi
}

# Función para inicializar swarm con IP detectada
init_swarm_auto() {
    local ip=$(detect_best_ip)
    
    if [ -z "$ip" ]; then
        echo "❌ No se pudo detectar una IP válida"
        exit 1
    fi
    
    echo "🚀 Inicializando Swarm con IP: $ip"
    
    if validate_ip $ip; then
        docker swarm init --advertise-addr $ip
        echo "✅ Swarm inicializado exitosamente"
    else
        echo "❌ Error: IP inválida o no disponible"
        exit 1
    fi
}

# Función interactiva para seleccionar IP
select_ip_interactive() {
    echo "🔍 Selecciona la IP para Docker Swarm:"
    echo ""
    
    # Mostrar opciones
    local ips=($(ip addr show | grep -E "inet (192\.168\.|10\.|172\.(1[6-9]|2[0-9]|3[0-1])\.)" | awk '{print $2}' | cut -d'/' -f1))
    
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
        *)
            echo "Uso: $0 [auto|detect|interactive|validate]"
            echo ""
            echo "Opciones:"
            echo "  auto        - Detectar y usar automáticamente"
            echo "  detect      - Solo mostrar IPs detectadas"
            echo "  interactive - Seleccionar IP manualmente"
            echo "  validate    - Validar una IP específica"
            exit 1
            ;;
    esac
}

# Ejecutar
main "$@"