package com.collegeroom.allocationsystem;

import com.collegeroom.allocationsystem.model.Booking;
import com.collegeroom.allocationsystem.model.BookingStatus;
import com.collegeroom.allocationsystem.model.User;
import com.collegeroom.allocationsystem.repository.BookingRepository;
import com.collegeroom.allocationsystem.repository.RoomRepository;
import com.collegeroom.allocationsystem.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ScheduleController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ScheduleController(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/schedule")
    public String viewSchedule(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String department,
            Model model) {

        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.APPROVED);

        if (roomId != null) {
            bookings = bookings.stream()
                    .filter(b -> b.getRoom().getId().equals(roomId))
                    .collect(Collectors.toList());
        }
        if (date != null) {
            bookings = bookings.stream()
                    .filter(b -> b.getDate().equals(date))
                    .collect(Collectors.toList());
        }
        if (department != null && !department.trim().isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> b.getRequestedBy().getDepartment() != null
                            && b.getRequestedBy().getDepartment().equalsIgnoreCase(department.trim()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("rooms", roomRepository.findAll());

        List<String> departments = userRepository.findAll().stream()
                .map(User::getDepartment)
                .filter(d -> d != null && !d.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("departments", departments);

        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedDepartment", department);

        return "schedule";
    }
}
