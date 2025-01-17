package com.example.JWTlogin.Service;

import com.example.JWTlogin.DTO.TicketRequest;
import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Model.Ticket;
import com.example.JWTlogin.Repository.TicketRepository;
import com.example.JWTlogin.Repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Slf4j
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;

    public Ticket savedTicket(TicketRequest ticketRequest, String username) {
        Optional<Ticket> TicketOptional = ticketRepository.findById(ticketRequest.ticketid());
        if (TicketOptional.isPresent()) {
            throw new RuntimeException("Ticket is already present");
        } else {

            //Optional<String> optionalUsername = Optional.ofNullable(username.toString().substring(10,22));
            //String actualUsername = optionalUsername.orElseThrow(() -> new RuntimeException("Username is empty"));
            log.info("Checking user " + username);

            Optional<AppUser> userOptional = userRepository.findByEmail(username);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            // Use ifPresent to safely access the value from Optional
            userOptional.ifPresent(user -> log.info("Useroptional " + user.getEmail()));

            Ticket a = new Ticket();
            a.setPrice(ticketRequest.Price());
            a.setPublishBy(userOptional.get());
            a.setCreatedAt(String.valueOf(new Date()));

            Ticket savedTicket = ticketRepository.save(a);
            log.info("Saved ticket: " + savedTicket);

            return savedTicket;
        }
    }


    public List<Ticket> getallticket(){
        return ticketRepository.findAll();
    }
}
