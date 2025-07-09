package com.minishop.productsservice.controller;

import com.minishop.productsservice.dto.ProductDto;
import com.minishop.productsservice.dto.ProductEvent;
import com.minishop.productsservice.service.ProductEventService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductEventService productEventService;

    // Simulación de base de datos en memoria
    private static final ConcurrentHashMap<Long, ProductDto> products = new ConcurrentHashMap<>();
    private static long nextId = 1L;

    // Inicializar datos al cargar la clase
    static {
        initializeProducts();
    }

    private static void initializeProducts() {
        for (int i = 1; i <= 5; i++) {
            ProductDto product = new ProductDto();
            product.setId((long) i);
            product.setName("Product-" + i);
            product.setPrice(100.0 * i);
            products.put((long) i, product);
            nextId = i + 1;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        ProductDto product = products.get(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        productDto.setId(nextId++);
        products.put(productDto.getId(), productDto);
        
        // Publicar evento de producto creado usando NATS
        ProductEvent productEvent = new ProductEvent(
            productDto.getId().toString(),
            productDto.getName(),
            productDto.getPrice(),
            "CREATED"
        );
        productEvent.setData(productDto);
        productEventService.publishProductCreated(productEvent);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        if (products.containsKey(id)) {
            productDto.setId(id);
            products.put(id, productDto);
            
            // Publicar evento de producto actualizado usando NATS
            ProductEvent productEvent = new ProductEvent(
                productDto.getId().toString(),
                productDto.getName(),
                productDto.getPrice(),
                "UPDATED"
            );
            productEvent.setData(productDto);
            productEventService.publishProductUpdated(productEvent);
            
            return ResponseEntity.ok(productDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (products.containsKey(id)) {
            ProductDto productDto = products.get(id);
            products.remove(id);
            
            // Publicar evento de producto eliminado usando NATS
            ProductEvent productEvent = new ProductEvent(
                id.toString(),
                productDto.getName(),
                productDto.getPrice(),
                "DELETED"
            );
            productEvent.setData(productDto);
            productEventService.publishProductDeleted(productEvent);
            
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/low-stock")
    public ResponseEntity<String> simulateLowStock(@PathVariable Long id) {
        if (products.containsKey(id)) {
            ProductDto productDto = products.get(id);
            
            // Publicar evento de stock bajo usando NATS
            ProductEvent productEvent = new ProductEvent(
                id.toString(),
                productDto.getName(),
                productDto.getPrice(),
                "LOW_STOCK"
            );
            productEvent.setData(productDto);
            productEventService.publishLowStock(productEvent);
            
            return ResponseEntity.ok("Low stock event published for product: " + productDto.getName());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
