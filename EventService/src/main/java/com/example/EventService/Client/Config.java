package com.example.EventService.Client;

import com.example.EventService.Strategy.ConcertSeatArrangementStrategy;
import com.example.EventService.Strategy.MovieSeatArrangementStrategy;
import com.example.EventService.Strategy.SeatArrangementStrategyFactory;
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