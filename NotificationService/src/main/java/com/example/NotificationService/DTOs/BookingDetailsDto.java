package com.example.NotificationService.DTOs;

import lombok.Data;

import javax.print.DocFlavor;
@Data
public class BookingDetailsDto {
    private String name;
    private String event;
    private  String date;
    private String seat;
    private String email;
}
