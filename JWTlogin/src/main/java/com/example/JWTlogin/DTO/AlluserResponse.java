package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.AppUser;
import lombok.Builder;

import java.util.List;
@Builder
public record AlluserResponse(List<AppUser>alluser) {
}
