package com.collegeroom.allocationsystem.repository;

import com.collegeroom.allocationsystem.model.Booking;
import com.collegeroom.allocationsystem.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomIdAndDate(Long roomId, LocalDate date);

    List<Booking> findByRequestedById(Long userId);

    List<Booking> findByStatusAndRequestedBy_Department(BookingStatus status, String department);

    List<Booking> findByRequestedByDepartment(String department);

    List<Booking> findByStatus(BookingStatus status);
}