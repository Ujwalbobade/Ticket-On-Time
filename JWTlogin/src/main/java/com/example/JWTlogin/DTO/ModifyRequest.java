package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.Role;
import lombok.Builder;

@Builder
public record ModifyRequest(String email, String password,String name, Role role,Integer PhoneNo) {

}
