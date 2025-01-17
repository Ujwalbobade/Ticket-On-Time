package com.example.OrderService.Models;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
@Data
public class Product {
    private  String name;
    private Long price;
}
