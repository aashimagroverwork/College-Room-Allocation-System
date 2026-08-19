package com.collegeroom.allocationsystem.service;

import com.collegeroom.allocationsystem.model.Booking;
import com.collegeroom.allocationsystem.model.BookingStatus;
import com.collegeroom.allocationsystem.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingConflictService {

    private final BookingRepository bookingRepository;

    public BookingConflictService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean hasConflict(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> existingBookings = bookingRepository.findByRoomIdAndDate(roomId, date);

        for (Booking booking : existingBookings) {
            if (booking.getStatus() == BookingStatus.APPROVED) {
                boolean overlaps = startTime.isBefore(booking.getEndTime())
                        && endTime.isAfter(booking.getStartTime());
                if (overlaps) {
                    return true;
                }
            }
        }
        return false;
    }
}