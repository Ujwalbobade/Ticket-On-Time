package com.example.JWTlogin.DTO;

import com.example.JWTlogin.Model.Ticket;
import lombok.Builder;

import java.util.List;
@Builder
public record AllTicketResponse(List<Ticket> allticket) {
}
