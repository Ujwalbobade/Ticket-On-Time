package com.example.Ticket.Service.Controller;

import com.example.Ticket.Service.DTOs.TicketRequestDTO;
import com.example.Ticket.Service.MongoModel.Ticket;
import com.example.Ticket.Service.Service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Ticket")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class TicketController {

    @Autowired
    private TicketService ticketService;


    private static final String REDIS_PREFIX = "Ticket_";

    @PostMapping("/Create")
    public Ticket createTicket(@ModelAttribute TicketRequestDTO ticketRequestDTO) {
        log.info("Creating ticket: {}", ticketRequestDTO);

        // Create the ticket
        Ticket createdTicket = ticketService.createTicket(ticketRequestDTO);

        // Publish the created ticket to Kafka
       // producerService.sendMessage(createdTicket);
        log.info("Published ticket to Kafka: {}", createdTicket);

        // Cache the created ticket in Redis for 10 minutes
        String cacheKey = REDIS_PREFIX + createdTicket.getTicketNo();
        //redisTemplate.opsForValue().set(cacheKey, createdTicket, Duration.ofMinutes(10));
        log.info("Cached ticket in Redis: {}", createdTicket);

        return createdTicket;
    }

    @GetMapping("/{TicketNo}")
    public Ticket getTicket(@PathVariable String TicketNo) {
        String cacheKey = REDIS_PREFIX + TicketNo;

        // Check Redis cache first
       // Ticket ticketFromCache = redisTemplate.opsForValue().get(cacheKey);
       // if (ticketFromCache != null) {
        //    log.info("Retrieved ticket from Redis cache: {}", ticketFromCache);
        //    return ticketFromCache;
       // }

        // If not in cache, retrieve from database
        Optional<Ticket> ticketFromDb = ticketService.Get_ticket_with_ticketNo(TicketNo);
        if (ticketFromDb.isPresent()) {
            // Cache the ticket in Redis for 10 minutes
         //   redisTemplate.opsForValue().set(cacheKey, ticketFromDb.get(), Duration.ofMinutes(10));
            log.info("Retrieved ticket from database and cached in Redis: {}", ticketFromDb.get());
            return ticketFromDb.get();
        } else {
            throw new RuntimeException("Ticket not found");
        }
    }

    @GetMapping("/GetvalidTickets")
    public List<Ticket> getAllTickets() {
        List<Ticket> validTickets = ticketService.getallValidtickets();
        log.info("Retrieved all valid tickets from database: {}", validTickets);

        // Optional: You can cache the list or use individual caching if needed

        return validTickets;
    }
}
