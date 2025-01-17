package com.example.OrderService.Controller;

import com.example.OrderService.DTOs.OrderDTO;
import com.example.OrderService.DTOs.ProductDTO;
import com.example.OrderService.Models.Order;
import com.example.OrderService.Models.Product;
import com.example.OrderService.Service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    public OrderService orderService;
    @Autowired
    private RestTemplate restTemplate;
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8080/products/";
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/ProductCatalogService2024")
    public String getproduct_with_id() {
        // Replace "other-service-name" with the actual service name registered in Eureka
        String url = "http://ProductCatalogService2024/product/{id]";
        return restTemplate.getForObject(url, String.class);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<String> createOrder(@RequestBody OrderDTO orderDTO) {
        logger.debug("Received request to create order with DTO: {}", orderDTO);
        //logger.info(orderDTO.getListOfAllProductIDs().toString());

        try {
            logger.info("Order  for user ID: {}", orderDTO.getUserId());
            logger.info("Order  total: {}", orderDTO.getTotalAmount());

            // Validate orderDTO
            if (orderDTO.getUserId() == null) {
                throw new IllegalArgumentException("User ID is required.");
            }

            if (orderDTO.getListOfAllProductIDs() == null || orderDTO.getListOfAllProductIDs().isEmpty()) {
                throw new IllegalArgumentException("Product list is missing or empty.");
            }

            if (orderDTO.getTotalAmount() <= 0) {
                throw new IllegalArgumentException("Total amount must be greater than zero.");
            }

            // Map OrderDTO to Order entity (if necessary)
            // Example: Order order = mapOrderDTOToEntity(orderDTO);

            // Call service method to save the order
            OrderDTO savedOrder = orderService.createOrder(orderDTO);

            if (savedOrder != null) {
                logger.info("Order created successfully for user ID: {}", orderDTO.getUserId());
                return ResponseEntity.ok("Order created successfully");
            } else {
                logger.error("Failed to create order for user ID: {}", orderDTO.getUserId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Validation failed for order request: {}", orderDTO, e);
            // Handle validation failure
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to create order for user ID: {}", orderDTO.getUserId(), e);
            // Handle other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order: " + e.getMessage());
        }
    }
    @GetMapping("/OrderDetails/{userid}")
    public ResponseEntity<List<Order>> get_orderdetails_of_user(@PathVariable Long userid){
        logger.info("For user ID:"+userid);
        List<Order> order = orderService.getOrderDetails(userid);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{userid}")
    public ResponseEntity<List<Order>> get_orderdetails_with_name(@PathVariable Long userid){
        logger.info("For user id"+userid);
        List<Order> products=orderService.getOrderDetailswithname(userid);
        return ResponseEntity.ok(products);
    }


}
