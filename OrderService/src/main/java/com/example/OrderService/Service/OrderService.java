package com.example.OrderService.Service;

import com.example.OrderService.DTOs.OrderDTO;
import com.example.OrderService.Models.Order;
import com.example.OrderService.Models.OrderStatus;
import com.example.OrderService.Models.Product;
import com.example.OrderService.Models.ProductQuantity;
import com.example.OrderService.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8087/products/";

    @Autowired
    private RestTemplate restTemplate; // Autowire RestTemplate to make HTTP requests

    // Method to fetch order details for a given userId and list of productIds
    public List<Order> getOrderDetails(Long userId) {
        List<Order> orders = new ArrayList<>();
        log.info("checking with userid" + userId);
        try {
            // Attempt to fetch orders by userId
            orders = orderRepository.findByUserId(userId);

            // Example of fetching product details from another service
            /* List<ProductDTO> products = new ArrayList<>();
            for (Order order : orders) {
                for (Long productId : order.getProducts()) {
                    String productUrl = "http://product-service/api/products/" + productId;
                    ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);
                    if (product != null) {
                        products.add(product);
                    }
                }
            } */

        } catch (Exception e) {
            // Handle any exceptions that may occur during data fetching
            System.err.println("Error fetching order details: " + e.getMessage());
            // Optionally, you can log the exception or perform recovery actions
        }
        if (orders == null) {
            log.info("order not found with id" + userId);
        }

        return orders;

        // Calculate total price of all products
        // double total = calculateTotalPrice(products);

        // Create OrderDTO and set values
        //OrderDTO order = new OrderDTO();
        //order.setUserId(userId); // Set userId in OrderDTO
        // order.setListOfAllProductIDs((products)); // Set product IDs
        // order.setTotalAmount(s.getTotal_price()); // Set total price

        // return order;
        //return null;
    }

    public Product getProductById(String productId) {
        log.info(PRODUCT_SERVICE_URL + productId);
        ResponseEntity<Product> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + productId, Product.class);
        return response.getBody();
    }

    // Method to create an order
    public OrderDTO createOrder(OrderDTO orderreq) {
        // Here you would typically implement logic to persist the order,
        // such as saving it to a database or sending it to another service.

        // For demonstration, let's assume some basic validation:
        if (orderreq.getUserId() == null
                || orderreq.getListOfAllProductIDs().isEmpty()
                || orderreq.getTotalAmount() <= 0) {
            // Handle the case where validation fails
            // For example, throw an exception, return an error response, or log the issue
            throw new IllegalArgumentException("Invalid order request: User ID, product list, or total amount is missing or invalid.");
        }
        List<ProductQuantity> listofproduct = new ArrayList<>();
        log.info("initalizing list");
        // Map ProductQuantity from orderreq.getAllProducts() to Order.ProductQuantity
        for (ProductQuantity pq : orderreq.getAllProductsforproductvity()) {
            Product a = getProductById(pq.getProductId());
            ProductQuantity orderPQ = new ProductQuantity(a.getName(), pq.getQuantity());
            // Map quantity from ProductQuantity
            listofproduct.add(orderPQ);
        }
        log.info("list is prepared " + listofproduct);
        log.info("converting order dto to order");
        // You can implement further business logic or integration here
        Order order = new Order();
        order.setUserId(orderreq.getUserId());
        log.info("order ID is set");// Set userId in OrderDTO
        order.setProducts(orderreq.getListOfAllProductIDs()); // Set product IDs
        order.setTotal_price(orderreq.getTotalAmount());
        log.info(orderreq.getStatus());
        log.info(orderreq.getStatus());

        OrderStatus orderStatus;

// Check if status is not null and if it's either PENDING or COMPLETE
        if (orderreq.getStatus() != null) {
            try {
                orderStatus = OrderStatus.valueOf(orderreq.getStatus().toUpperCase());
                if (orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.COMPLETE) {
                    order.setStatus(orderStatus);
                } else {
                    // Set status to UNKNOWN if it doesn't match PENDING or COMPLETE
                    order.setStatus(OrderStatus.UNKNOWN);
                }
            } catch (IllegalArgumentException e) {
                // Set status to UNKNOWN if the status value is invalid
                order.setStatus(OrderStatus.UNKNOWN);
            }
        } else {
            // Set status to UNKNOWN if it's null
            order.setStatus(OrderStatus.UNKNOWN);
        }

// Log the status
        log.info(order.getStatus().toString());
        log.info("order is" + order.getProducts());
        log.info("order is saving");
        //save into database
        orderRepository.save(order);
        log.info("order saved");


        return order_wrapper(order);// Return the created order
    }

    // Utility method to calculate total price of products


    // Utility method to get list of product IDs from List<ProductDTO>


    private OrderDTO order_wrapper(Order ord) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(ord.getUserId()); // Assuming getUserId() method exists in Order entity
        orderDTO.setListOfAllProductIDs(ord.getProducts()); // Assuming getListOfProducts() method exists in Order entity
        orderDTO.setTotalAmount(ord.getTotal_price()); // Assuming getTotalPrice() method exists in Order entity

        return orderDTO;
    }

    public List<Order> getOrderDetailswithname(Long userid) {
        List<Order> orders = new ArrayList<>();
        log.info("checking with userid" + userid);
        try {
            // Attempt to fetch orders by userId
            orders = orderRepository.findByUserId(userid);

            // Example of fetching product details from another service
            //List<ProductDTO> products = new ArrayList<>();

            for (Order order : orders) {
                Map<String, Long> products = new HashMap<>();

                for (Map.Entry<String, Long> entry : order.getProducts().entrySet()) {
                    String productUrl = PRODUCT_SERVICE_URL + entry.getKey();
                    Product a = restTemplate.getForObject(productUrl, Product.class);
                    products.put(a.getName(), entry.getValue());

                }
                order.setProducts(products);
            }
            return orders;


        } catch (Exception r) {

        }
        return  null;
    }
}
