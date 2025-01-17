package com.example.BookingService.DTOs;

import com.example.BookingService.Model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Boolean status;

    private  Long booking_id;
    private String Seatnumber;

    private String eventname;

    private Double price;

}
