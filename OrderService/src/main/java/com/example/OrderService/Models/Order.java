package com.example.OrderService.Models;

import com.example.OrderService.DTOs.ProductDTO;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document
@Data
public class Order extends BaseModel{
    @Id
    private String id;

    private Map<String,Long> products;

    private long total_price;
    private long userId;

    private OrderStatus Status;

    private String Phoneno;

    // Method to add or update product quantity



}
