package com.example.JWTlogin.Repository;

import com.example.JWTlogin.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long ticketid);

    Ticket save(Ticket entity);
}
