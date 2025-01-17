package com.example.BookingService.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
public class TicketRequestDTO implements Serializable {
    private String ticketType;

    private String description;
    @JsonIgnore
    private MultipartFile ticket_file;
    private String TicketNo;

    private String status;
    private Long price;
    private String From;
    private String To;
    private Long postedByUserId;

    private Boolean ValidationStatus;
}
