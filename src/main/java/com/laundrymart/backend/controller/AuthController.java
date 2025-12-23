package com.laundrymart.backend.controller;

import com.laundrymart.backend.config.JwtUtil;
import com.laundrymart.backend.entity.User;
import com.laundrymart.backend.repository.UserRepository;
import com.laundrymart.backend.dto.ProfileUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // For React frontend
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());
        return ResponseEntity.ok(Map.of("token", token, "user", savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        var userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "email", user.getEmail(),
                        "fullName", user.getFullName(),
                        "phone", user.getPhone(),
                        "address", user.getAddress()
                )
        ));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDto updateDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();

        // Check for unique email if changing
        if (updateDto.getEmail() != null && !updateDto.getEmail().trim().isEmpty() && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateDto.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already in use");
            }
            user.setEmail(updateDto.getEmail().trim());
        }

        if (updateDto.getFullName() != null && !updateDto.getFullName().trim().isEmpty()) {
            user.setFullName(updateDto.getFullName().trim());
        }
        if (updateDto.getPhone() != null && !updateDto.getPhone().trim().isEmpty()) {
            user.setPhone(updateDto.getPhone().trim());
        }
        if (updateDto.getAddress() != null && !updateDto.getAddress().trim().isEmpty()) {
            user.setAddress(updateDto.getAddress().trim());
        }
        if (updateDto.getPassword() != null && !updateDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        try {
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "user", Map.of(
                            "id", updatedUser.getId(),
                            "username", updatedUser.getUsername(),
                            "role", updatedUser.getRole(),
                            "email", updatedUser.getEmail(),
                            "fullName", updatedUser.getFullName(),
                            "phone", updatedUser.getPhone(),
                            "address", updatedUser.getAddress()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update profile: " + e.getMessage());
        }
    }
}