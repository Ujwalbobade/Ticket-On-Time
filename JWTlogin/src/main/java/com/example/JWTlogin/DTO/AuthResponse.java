package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Model.Role;
import com.example.JWTlogin.Service.UserDetails.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

@Builder
public record AuthResponse(String token, AuthStatus authStatus) {
}
