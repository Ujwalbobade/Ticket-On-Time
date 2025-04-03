package com.example.JWTlogin.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Ticket extends BaseModel {
    @Enumerated(EnumType.STRING)
    Tickettype tickettype;

    private BigDecimal price;

    @ManyToOne
    private AppUser PublishBy;




}
