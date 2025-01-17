package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.Role;
import lombok.Builder;

@Builder
public record LoginResponse(String token, AuthStatus authStatus, Role userrole,String username,Long id) {
}
