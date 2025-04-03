package com.example.BookingService.Repository;




import com.example.BookingService.Model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    // Add custom queries here if needed
    Optional<Seat> findBySeatNumber(String seatNumber);
    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber AND s.event.id = :eventId")
    Optional<Seat> findBySeatNumberAndEventId(@Param("seatNumber") String seatNumber, @Param("eventId") Long eventId);
    @Query("SELECT s FROM Seat s WHERE  s.event.id = :eventId")
    Optional<List<Seat>> findBySeatWithEventId(@Param("eventId") Long eventId);

}