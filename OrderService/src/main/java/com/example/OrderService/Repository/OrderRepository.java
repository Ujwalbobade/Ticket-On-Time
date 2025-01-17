package com.example.OrderService.Repository;

import com.example.OrderService.Models.Order;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableMongoRepositories

public interface OrderRepository extends MongoRepository<Order,String> {

    List<Order> findByUserId(Long userId);

    @Override
     Order save(Order entity);
}
