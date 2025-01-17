package com.example.Ticket.Service.DTOs;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDto {
    private String Seatno;
    private Long userid;

    private String Eventname;

}
