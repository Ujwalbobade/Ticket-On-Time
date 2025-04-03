package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record SignupRequest(String email, String password, String name, Role role) {
}
