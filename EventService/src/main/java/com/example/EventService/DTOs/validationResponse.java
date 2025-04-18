package com.example.EventService.DTOs;

import lombok.Data;

@Data
public class validationResponse {
    private String id;

    private  String userrole;

    private String username;

    private  boolean tokenvalidation;

    private String email;
}
