import { ref, reactive, onMounted } from 'vue';
import axios from 'axios';

const App = {
  setup() {
    // Estado reactivo
    const products = ref([]);
    const loading = ref(false);
    const error = ref('');
    const showForm = ref(false);
    const editingProduct = ref(null);
    
    // Formulario reactivo
    const form = reactive({
      id: null,
      name: '',
      price: 0
    });

    // Configuración de axios
    const api = axios.create({
      baseURL: '/products',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // Cargar todos los productos
    const loadProducts = async () => {
      try {
        loading.value = true;
        error.value = '';
        const response = await api.get('');
        products.value = response.data;
      } catch (err) {
        error.value = 'Error al cargar los productos: ' + err.message;
        console.error('Error loading products:', err);
      } finally {
        loading.value = false;
      }
    };

    // Crear nuevo producto
    const createProduct = async () => {
      try {
        loading.value = true;
        error.value = '';
        const productData = {
          name: form.name,
          price: form.price
        };
        await api.post('', productData);
        await loadProducts();
        resetForm();
        showForm.value = false;
      } catch (err) {
        error.value = 'Error al crear el producto: ' + err.message;
        console.error('Error creating product:', err);
      } finally {
        loading.value = false;
      }
    };

    // Actualizar producto existente
    const updateProduct = async () => {
      try {
        loading.value = true;
        error.value = '';
        const productData = {
          name: form.name,
          price: form.price
        };
        await api.put(`/${form.id}`, productData);
        await loadProducts();
        resetForm();
        showForm.value = false;
        editingProduct.value = null;
      } catch (err) {
        error.value = 'Error al actualizar el producto: ' + err.message;
        console.error('Error updating product:', err);
      } finally {
        loading.value = false;
      }
    };

    // Eliminar producto
    const deleteProduct = async (productId) => {
      if (!confirm('¿Estás seguro de que quieres eliminar este producto?')) {
        return;
      }
      
      try {
        loading.value = true;
        error.value = '';
        await api.delete(`/${productId}`);
        await loadProducts();
      } catch (err) {
        error.value = 'Error al eliminar el producto: ' + err.message;
        console.error('Error deleting product:', err);
      } finally {
        loading.value = false;
      }
    };

    // Simular stock bajo
    const simulateLowStock = async (productId) => {
      try {
        loading.value = true;
        error.value = '';
        const response = await api.post(`/${productId}/low-stock`);
        // Mostrar mensaje de éxito temporal
        const successMsg = `Stock bajo simulado para el producto`;
        error.value = '';
        
        // Mostrar notificación temporal de éxito
        const tempDiv = document.createElement('div');
        tempDiv.className = 'alert alert-success';
        tempDiv.innerHTML = `
          <span>${successMsg}</span>
          <button onclick="this.parentElement.remove()" class="btn-close">✕</button>
        `;
        document.querySelector('.container').insertBefore(tempDiv, document.querySelector('.orders-section'));
        
        setTimeout(() => {
          if (tempDiv.parentElement) {
            tempDiv.remove();
          }
        }, 3000);
        
      } catch (err) {
        error.value = 'Error al simular stock bajo: ' + err.message;
        console.error('Error simulating low stock:', err);
      } finally {
        loading.value = false;
      }
    };

    // Resetear formulario
    const resetForm = () => {
      form.id = null;
      form.name = '';
      form.price = 0;
    };

    // Editar producto
    const editProduct = (product) => {
      editingProduct.value = product;
      form.id = product.id;
      form.name = product.name;
      form.price = product.price;
      showForm.value = true;
    };

    // Cancelar edición
    const cancelEdit = () => {
      resetForm();
      showForm.value = false;
      editingProduct.value = null;
    };

    // Enviar formulario
    const submitForm = () => {
      if (!form.name.trim()) {
        error.value = 'El nombre del producto es requerido';
        return;
      }
      
      if (form.price < 0) {
        error.value = 'El precio debe ser mayor o igual a 0';
        return;
      }

      if (editingProduct.value) {
        updateProduct();
      } else {
        createProduct();
      }
    };

    // Formatear precio
    const formatPrice = (price) => {
      return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'EUR'
      }).format(price);
    };

    // Cargar productos al montar el componente
    onMounted(() => {
      loadProducts();
    });

    return {
      products,
      loading,
      error,
      showForm,
      editingProduct,
      form,
      loadProducts,
      createProduct,
      updateProduct,
      deleteProduct,
      simulateLowStock,
      editProduct,
      cancelEdit,
      submitForm,
      resetForm,
      formatPrice
    };
  },

  template: `
    <div class="container">
      <header class="header">
        <h1>🛍️ Gestión de Productos</h1>
        <button 
          @click="showForm = true; resetForm()" 
          class="btn btn-primary"
          :disabled="loading"
        >
          ➕ Nuevo Producto
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

      <!-- Formulario de producto -->
      <div v-if="showForm" class="modal-overlay">
        <div class="modal">
          <div class="modal-header">
            <h2>{{ editingProduct ? 'Editar' : 'Nuevo' }} Producto</h2>
            <button @click="cancelEdit()" class="btn-close">✕</button>
          </div>
          
          <form @submit.prevent="submitForm()" class="form">
            <div class="form-group">
              <label for="productName">Nombre del Producto:</label>
              <input 
                id="productName"
                v-model="form.name" 
                type="text" 
                class="form-control"
                placeholder="Ingresa el nombre del producto"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="productPrice">Precio (€):</label>
              <input 
                id="productPrice"
                v-model.number="form.price" 
                type="number" 
                class="form-control"
                min="0"
                step="0.01"
                placeholder="0.00"
                required
              />
            </div>
            
            <div class="form-actions">
              <button type="button" @click="cancelEdit()" class="btn btn-secondary">
                Cancelar
              </button>
              <button type="submit" class="btn btn-primary" :disabled="loading">
                {{ editingProduct ? 'Actualizar' : 'Crear' }} Producto
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Lista de productos -->
      <div class="orders-section">
        <div class="section-header">
          <h2>Productos ({{ products.length }})</h2>
          <button @click="loadProducts()" class="btn btn-outline" :disabled="loading">
            🔄 Refrescar
          </button>
        </div>

        <div v-if="products.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">📦</div>
          <h3>No hay productos</h3>
          <p>Crea tu primer producto para comenzar</p>
        </div>

        <div v-else class="orders-grid">
          <div 
            v-for="product in products" 
            :key="product.id" 
            class="order-card product-card"
          >
            <div class="order-header">
              <span class="order-id">#{{ product.id }}</span>
              <div class="order-actions">
                <button 
                  @click="editProduct(product)" 
                  class="btn btn-small btn-outline"
                  title="Editar producto"
                >
                  ✏️
                </button>
                <button 
                  @click="deleteProduct(product.id)" 
                  class="btn btn-small btn-danger"
                  title="Eliminar producto"
                  :disabled="loading"
                >
                  🗑️
                </button>
              </div>
            </div>
            
            <div class="order-content">
              <div class="product-name">
                <h3>{{ product.name }}</h3>
              </div>
              
              <div class="order-field">
                <span class="field-label">Precio:</span>
                <span class="field-value price-value">{{ formatPrice(product.price) }}</span>
              </div>
            </div>

            <div class="order-footer">
              <div class="status-actions">
                <button 
                  @click="simulateLowStock(product.id)"
                  class="btn btn-small btn-warning"
                  :disabled="loading"
                  title="Simular stock bajo"
                >
                  ⚠️ Stock Bajo
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