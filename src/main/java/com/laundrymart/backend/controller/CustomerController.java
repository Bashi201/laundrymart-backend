package com.laundrymart.backend.controller;

import com.laundrymart.backend.entity.Order;
import com.laundrymart.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")  // Only users with role CUSTOMER can access
public class CustomerController {

    @Autowired
    private OrderService orderService;

    /**
     * POST /customer/orders
     * Customer places a new order
     * Body: { "details": "5 shirts, 2 pants" }
     */
    @PostMapping("/orders")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    /**
     * GET /customer/orders
     * Customer gets their own orders (no need for customerId in path)
     */
    @GetMapping("/orders")
    public List<Order> getMyOrders() {
        return orderService.getOrdersByCustomer();
    }
}
//test