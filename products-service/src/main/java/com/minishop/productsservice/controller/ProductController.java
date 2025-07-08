package com.minishop.productsservice.controller;

import com.minishop.productsservice.dto.ProductDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        ProductDto product = new ProductDto();
        product.setId(id);
        product.setName("Product-" + id);
        product.setPrice(100.0 * id);
        return product;
    }
}
