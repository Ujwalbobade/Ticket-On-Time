package com.example.BookingService.DTOs;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ValidationResDTo implements Serializable {
    private String Text;

    private String Status;
    private String validationerror;



}
