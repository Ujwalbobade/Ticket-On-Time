package com.example.EventService.DTOs;

import lombok.Data;

import java.util.List;
@Data

public class BookingRequestDto {

    private List<String> Seatno;

    private  Long userid;

    private String Eventname;

    private String eventtype;
}
