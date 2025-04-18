package com.example.EventService.Exceptions;

public class SeatException extends RuntimeException {

    private static final long serialVersionUID=1L;

    public SeatException(String message){super(message);
    }

    public SeatException(String message,Throwable cause){
        super(message,cause);
    }

}
