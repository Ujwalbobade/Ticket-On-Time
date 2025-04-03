package com.example.JWTlogin.Service;

import com.example.JWTlogin.Model.Role;

public interface Authservice {

    String Login(String Email,String Password);

     String SignUp(String Email, String Password, String Name, Role role);
}
