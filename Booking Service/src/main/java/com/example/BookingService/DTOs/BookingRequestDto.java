package com.example.BookingService.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class BookingRequestDto {
    private List<String> Seatno;
    private Long userid;
    private String Eventname;

    public Long getUserid() {
        return userid;
    }

    // Setter for userid
    public void setUserid(Long userid) {
        this.userid = userid;
    }

    // Getter for Eventname
    public String getEventname() {
        return Eventname;
    }

    // Setter for Eventname
    public void setEventname(String Eventname) {
        this.Eventname = Eventname;
    }
}
