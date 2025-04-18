package com.example.EventService.Repositories;

import com.example.EventService.Model.Seat;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    Optional<Seat> findBySeatNumber(String seatNumber);
    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber AND s.event.id = :eventId")
    Optional<Seat> findBySeatNumberAndEventId(@Param("seatNumber") String seatNumber, @Param("eventId") Long eventId);
    @Query("SELECT s FROM Seat s WHERE  s.event.id = :eventId")
    Optional<List<Seat>> findBySeatWithEventId(@Param("eventId") Long eventId);

    @Modifying
    @Query("UPDATE Seat s SET s.isAvailable = true, s.isLocked = false, s.lockedUntil = null " +
            "WHERE s.isLocked = true AND s.lockedUntil <= :currentTime")
    int unlockExpiredSeats(@Param("currentTime") LocalDateTime currentTime);
}
