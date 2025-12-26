package com.laundrymart.backend.service;

import com.laundrymart.backend.entity.User;
import com.laundrymart.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Add methods for update, delete, findById, etc.

    public ResponseEntity<?> deleteUser(Long userId) {
        var userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Cannot delete admin users");
        }

        userRepository.delete(user);
        return ResponseEntity.ok().body("User deleted successfully");
    }
}