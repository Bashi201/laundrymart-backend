package com.laundrymart.backend.service;

import com.laundrymart.backend.entity.Order;
import com.laundrymart.backend.entity.User;
import com.laundrymart.backend.repository.OrderRepository;
import com.laundrymart.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new order for the currently logged-in customer
     */
    public Order createOrder(Order order) {
        // Get current authenticated user (customer)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        order.setCustomer(customer);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Rider and employee are null initially
        order.setRider(null);
        order.setEmployee(null);

        return orderRepository.save(order);
    }

    /**
     * Get all orders placed by the currently logged-in customer
     */
    public List<Order> getOrdersByCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        return orderRepository.findByCustomerId(customer.getId());
    }

    /**
     * Get all orders in the system (for Admin)
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Admin assigns a rider to an order
     */
    public Order assignRider(Long orderId, Long riderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User rider = userRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("Rider not found"));

        if (!"RIDER".equals(rider.getRole())) {
            throw new IllegalArgumentException("Selected user is not a RIDER");
        }

        order.setRider(rider);
        // Optional: update status when rider is assigned
        // order.setStatus("PICKUP_SCHEDULED");

        return orderRepository.save(order);
    }

    /**
     * Admin assigns an employee (laundry worker) to an order
     */
    public Order assignEmployee(Long orderId, Long employeeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!"EMPLOYEE".equals(employee.getRole())) {
            throw new IllegalArgumentException("Selected user is not an EMPLOYEE");
        }

        order.setEmployee(employee);
        // Optional: update status when employee is assigned
        // order.setStatus("IN_PROCESSING");

        return orderRepository.save(order);
    }

    /**
     * Optional: Update order status manually (e.g., IN_TRANSIT, DELIVERED)
     */
    public Order updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Optional: Add validation for allowed statuses
        List<String> allowedStatuses = List.of("PENDING", "PICKUP_SCHEDULED", "IN_TRANSIT", "PROCESSING", "WASHING", "DELIVERED", "COMPLETED", "CANCELLED");
        if (!allowedStatuses.contains(newStatus.toUpperCase())) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        order.setStatus(newStatus.toUpperCase());
        return orderRepository.save(order);
    }
}