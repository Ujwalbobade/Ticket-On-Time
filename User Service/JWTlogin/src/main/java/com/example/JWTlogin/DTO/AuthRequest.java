package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record AuthRequest(String email, String password) {
}
