package com.example.Ticket.Service.MongoRepository;

import com.example.Ticket.Service.MongoModel.Ticket;
import com.example.Ticket.Service.MongoModel.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface TicketRepository extends MongoRepository<Ticket,String> {

    Optional<Ticket> findByTicketNo(String ticketNo);

    List<Ticket> findByStatus(TicketStatus status);
}
