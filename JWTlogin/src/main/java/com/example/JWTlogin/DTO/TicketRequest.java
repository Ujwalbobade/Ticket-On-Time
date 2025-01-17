package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Model.Ticket;
import com.example.JWTlogin.Model.Tickettype;

import java.math.BigDecimal;

public record TicketRequest(BigDecimal Price , Tickettype tickettype, Long ticketid) {
}
