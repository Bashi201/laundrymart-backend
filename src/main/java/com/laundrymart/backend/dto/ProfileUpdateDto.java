package com.laundrymart.backend.dto;

import lombok.Data;

@Data
public class ProfileUpdateDto {
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String password;  // Optional new password
}