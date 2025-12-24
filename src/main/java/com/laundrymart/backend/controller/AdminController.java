// Updated AdminController.java
package com.laundrymart.backend.controller;

import com.laundrymart.backend.entity.User;
import com.laundrymart.backend.entity.Order;
import com.laundrymart.backend.service.UserService;
import com.laundrymart.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/employees")
    public User addEmployee(@RequestBody User employee) {
        employee.setRole("EMPLOYEE");
        return userService.saveUser(employee);
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/orders/{orderId}/assign-rider")
    public Order assignRider(@PathVariable Long orderId, @RequestBody Map<String, Long> body) {
        return orderService.assignRider(orderId, body.get("riderId"));
    }

    @PutMapping("/orders/{orderId}/assign-employee")
    public Order assignEmployee(@PathVariable Long orderId, @RequestBody Map<String, Long> body) {
        return orderService.assignEmployee(orderId, body.get("employeeId"));
    }

    @PutMapping("/orders/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        return orderService.updateStatus(orderId, status.trim());
    }

    // Similar for riders, orders management, assign tasks
}