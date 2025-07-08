package com.minishop.orderservice.dto;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private String productName;
    private int quantity;
}
