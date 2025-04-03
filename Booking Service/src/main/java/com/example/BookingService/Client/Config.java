package com.example.BookingService.Client;

import com.example.BookingService.Strategy.ConcertSeatArrangementStrategy;
import com.example.BookingService.Strategy.MovieSeatArrangementStrategy;
import com.example.BookingService.Strategy.SeatArrangementStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class Config {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public SeatArrangementStrategyFactory seatArrangementStrategyFactory() {
        return new SeatArrangementStrategyFactory();
    }

    @Bean
    public MovieSeatArrangementStrategy movieSeatArrangementStrategy() {
        return new MovieSeatArrangementStrategy();
    }

    @Bean
    public ConcertSeatArrangementStrategy concertSeatArrangementStrategy() {
        return new ConcertSeatArrangementStrategy();
    }


}
