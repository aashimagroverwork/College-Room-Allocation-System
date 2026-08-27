package com.collegeroom.allocationsystem;

import com.collegeroom.allocationsystem.model.Booking;
import com.collegeroom.allocationsystem.model.BookingStatus;
import com.collegeroom.allocationsystem.model.Room;
import com.collegeroom.allocationsystem.repository.BookingRepository;
import com.collegeroom.allocationsystem.repository.RoomRepository;
import com.collegeroom.allocationsystem.service.BookingConflictService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingConflictService bookingConflictService;

    public AdminController(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            BookingConflictService bookingConflictService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bookingConflictService = bookingConflictService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<Booking> awaitingApprovals = bookingRepository.findByStatus(BookingStatus.HOD_APPROVED);
        List<Room> rooms = roomRepository.findAll();

        long totalRooms = roomRepository.count();
        long totalBookings = bookingRepository.count();
        long pendingHODApproved = awaitingApprovals.size();
        long approvedCount = bookingRepository.findByStatus(BookingStatus.APPROVED).size();
        long rejectedCount = bookingRepository.findByStatus(BookingStatus.REJECTED).size();

        model.addAttribute("awaitingApprovals", awaitingApprovals);
        model.addAttribute("rooms", rooms);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("pendingApprovals", pendingHODApproved);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "admin-dashboard";
    }

    @PostMapping("/admin/bookings/{id}/approve")
    public String approveBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        boolean conflict = bookingConflictService.hasConflict(
                booking.getRoom().getId(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (conflict) {
            return "redirect:/admin/dashboard?error=conflict";
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        return "redirect:/admin/dashboard?success=approved";
    }

    @PostMapping("/admin/bookings/{id}/reject")
    public String rejectBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        return "redirect:/admin/dashboard?success=rejected";
    }

    @PostMapping("/admin/rooms")
    public String addRoom(
            @RequestParam String name,
            @RequestParam String building,
            @RequestParam Integer capacity) {

        Room room = new Room();
        room.setName(name);
        room.setBuilding(building);
        room.setCapacity(capacity);

        roomRepository.save(room);

        return "redirect:/admin/dashboard?success=room_added";
    }

    @PostMapping("/admin/rooms/edit/{id}")
    public String editRoom(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String building,
            @RequestParam Integer capacity) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        room.setName(name);
        room.setBuilding(building);
        room.setCapacity(capacity);

        roomRepository.save(room);

        return "redirect:/admin/dashboard?success=room_updated";
    }

    @PostMapping("/admin/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        try {
            roomRepository.deleteById(id);
            return "redirect:/admin/dashboard?success=room_deleted";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=delete_failed";
        }
    }
}
