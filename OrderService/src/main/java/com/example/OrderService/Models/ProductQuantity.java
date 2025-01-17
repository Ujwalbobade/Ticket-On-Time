package com.example.OrderService.Models;

import lombok.Data;

@Data
public  class ProductQuantity {

    private String productId;
    private Long quantity;

    public ProductQuantity(String productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Constructors, getters, and setters
    // Omitted for brevity

}