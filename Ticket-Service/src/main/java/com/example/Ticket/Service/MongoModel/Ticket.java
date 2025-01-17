package com.example.Ticket.Service.MongoModel;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Document
public class Ticket  {
    @MongoId
    private String Id;
    @CreatedDate
    private Date CreatedAt;

    private TicketType ticketType;

    private String TicketNo;
    private String description;
    private String ticket_file_path;

    private TicketStatus status;


    private Long Price;
    private Long userid;
    private String  From;
    private String To;

    private Boolean ValidtionStatus = false;

    private Long postedByUserId;

    private String validationerror;

    private Boolean isBooked;

    private String seatNumber;

}
/*
ticket    user
1           1
             m
 */