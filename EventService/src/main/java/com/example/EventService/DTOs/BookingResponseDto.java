package com.example.EventService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Boolean status;

    private String Seatnumber;

    private String eventname;

    private Double price;

    private String etype;
}
