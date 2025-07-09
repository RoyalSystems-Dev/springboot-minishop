import { ref, reactive, onMounted } from 'vue';
import axios from 'axios';

const App = {
  setup() {
    // Estado reactivo
    const orders = ref([]);
    const loading = ref(false);
    const error = ref('');
    const showForm = ref(false);
    const editingOrder = ref(null);
    
    // Formulario reactivo
    const form = reactive({
      id: null,
      productName: '',
      quantity: 1
    });

    // Configuración de axios
    const api = axios.create({
      baseURL: '/orders',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // Cargar todas las órdenes
    const loadOrders = async () => {
      try {
        loading.value = true;
        error.value = '';
        const response = await api.get('');
        orders.value = response.data;
      } catch (err) {
        error.value = 'Error al cargar las órdenes: ' + err.message;
        console.error('Error loading orders:', err);
      } finally {
        loading.value = false;
      }
    };

    // Crear nueva orden
    const createOrder = async () => {
      try {
        loading.value = true;
        error.value = '';
        const orderData = {
          productName: form.productName,
          quantity: form.quantity
        };
        await api.post('', orderData);
        await loadOrders();
        resetForm();
        showForm.value = false;
      } catch (err) {
        error.value = 'Error al crear la orden: ' + err.message;
        console.error('Error creating order:', err);
      } finally {
        loading.value = false;
      }
    };

    // Actualizar orden existente
    const updateOrder = async () => {
      try {
        loading.value = true;
        error.value = '';
        const orderData = {
          productName: form.productName,
          quantity: form.quantity
        };
        await api.put(`/${form.id}`, orderData);
        await loadOrders();
        resetForm();
        showForm.value = false;
        editingOrder.value = null;
      } catch (err) {
        error.value = 'Error al actualizar la orden: ' + err.message;
        console.error('Error updating order:', err);
      } finally {
        loading.value = false;
      }
    };

    // Eliminar orden
    const deleteOrder = async (orderId) => {
      if (!confirm('¿Estás seguro de que quieres eliminar esta orden?')) {
        return;
      }
      
      try {
        loading.value = true;
        error.value = '';
        await api.delete(`/${orderId}`);
        await loadOrders();
      } catch (err) {
        error.value = 'Error al eliminar la orden: ' + err.message;
        console.error('Error deleting order:', err);
      } finally {
        loading.value = false;
      }
    };

    // Actualizar estado de orden
    const updateOrderStatus = async (orderId, status) => {
      try {
        loading.value = true;
        error.value = '';
        await api.put(`/${orderId}/status/${status}`);
        await loadOrders();
      } catch (err) {
        error.value = 'Error al actualizar el estado: ' + err.message;
        console.error('Error updating order status:', err);
      } finally {
        loading.value = false;
      }
    };

    // Resetear formulario
    const resetForm = () => {
      form.id = null;
      form.productName = '';
      form.quantity = 1;
    };

    // Editar orden
    const editOrder = (order) => {
      editingOrder.value = order;
      form.id = order.id;
      form.productName = order.productName;
      form.quantity = order.quantity;
      showForm.value = true;
    };

    // Cancelar edición
    const cancelEdit = () => {
      resetForm();
      showForm.value = false;
      editingOrder.value = null;
    };

    // Enviar formulario
    const submitForm = () => {
      if (!form.productName.trim()) {
        error.value = 'El nombre del producto es requerido';
        return;
      }
      
      if (form.quantity < 1) {
        error.value = 'La cantidad debe ser mayor a 0';
        return;
      }

      if (editingOrder.value) {
        updateOrder();
      } else {
        createOrder();
      }
    };

    // Cargar órdenes al montar el componente
    onMounted(() => {
      loadOrders();
    });

    return {
      orders,
      loading,
      error,
      showForm,
      editingOrder,
      form,
      loadOrders,
      createOrder,
      updateOrder,
      deleteOrder,
      updateOrderStatus,
      editOrder,
      cancelEdit,
      submitForm,
      resetForm
    };
  },

  template: `
    <div class="container">
      <header class="header">
        <h1>🛒 Gestión de Órdenes</h1>
        <button 
          @click="showForm = true; resetForm()" 
          class="btn btn-primary"
          :disabled="loading"
        >
          ➕ Nueva Orden
        </button>
      </header>

      <!-- Mensajes de error -->
      <div v-if="error" class="alert alert-error">
        {{ error }}
        <button @click="error = ''" class="btn-close">✕</button>
      </div>

      <!-- Indicador de carga -->
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        Cargando...
      </div>

      <!-- Formulario de orden -->
      <div v-if="showForm" class="modal-overlay">
        <div class="modal">
          <div class="modal-header">
            <h2>{{ editingOrder ? 'Editar' : 'Nueva' }} Orden</h2>
            <button @click="cancelEdit()" class="btn-close">✕</button>
          </div>
          
          <form @submit.prevent="submitForm()" class="form">
            <div class="form-group">
              <label for="productName">Nombre del Producto:</label>
              <input 
                id="productName"
                v-model="form.productName" 
                type="text" 
                class="form-control"
                placeholder="Ingresa el nombre del producto"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="quantity">Cantidad:</label>
              <input 
                id="quantity"
                v-model.number="form.quantity" 
                type="number" 
                class="form-control"
                min="1"
                required
              />
            </div>
            
            <div class="form-actions">
              <button type="button" @click="cancelEdit()" class="btn btn-secondary">
                Cancelar
              </button>
              <button type="submit" class="btn btn-primary" :disabled="loading">
                {{ editingOrder ? 'Actualizar' : 'Crear' }} Orden
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Lista de órdenes -->
      <div class="orders-section">
        <div class="section-header">
          <h2>Órdenes ({{ orders.length }})</h2>
          <button @click="loadOrders()" class="btn btn-outline" :disabled="loading">
            🔄 Refrescar
          </button>
        </div>

        <div v-if="orders.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">📦</div>
          <h3>No hay órdenes</h3>
          <p>Crea tu primera orden para comenzar</p>
        </div>

        <div v-else class="orders-grid">
          <div 
            v-for="order in orders" 
            :key="order.id" 
            class="order-card"
          >
            <div class="order-header">
              <span class="order-id">#{{ order.id }}</span>
              <div class="order-actions">
                <button 
                  @click="editOrder(order)" 
                  class="btn btn-small btn-outline"
                  title="Editar orden"
                >
                  ✏️
                </button>
                <button 
                  @click="deleteOrder(order.id)" 
                  class="btn btn-small btn-danger"
                  title="Eliminar orden"
                  :disabled="loading"
                >
                  🗑️
                </button>
              </div>
            </div>
            
            <div class="order-content">
              <div class="order-field">
                <span class="field-label">Producto:</span>
                <span class="field-value">{{ order.productName }}</span>
              </div>
              
              <div class="order-field">
                <span class="field-label">Cantidad:</span>
                <span class="field-value">{{ order.quantity }}</span>
              </div>
            </div>

            <div class="order-footer">
              <div class="status-actions">
                <button 
                  @click="updateOrderStatus(order.id, 'PENDING')"
                  class="btn btn-small btn-warning"
                  :disabled="loading"
                >
                  ⏳ Pendiente
                </button>
                <button 
                  @click="updateOrderStatus(order.id, 'COMPLETED')"
                  class="btn btn-small btn-success"
                  :disabled="loading"
                >
                  ✅ Completado
                </button>
                <button 
                  @click="updateOrderStatus(order.id, 'CANCELLED')"
                  class="btn btn-small btn-secondary"
                  :disabled="loading"
                >
                  ❌ Cancelado
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
};

export default App;