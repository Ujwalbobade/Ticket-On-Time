package com.example.BookingService.Repository;



import com.example.BookingService.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    Optional<Event> findByEventName(String eventName);



}
