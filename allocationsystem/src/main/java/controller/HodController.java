package com.collegeroom.allocationsystem.controller;

import com.collegeroom.allocationsystem.model.Booking;
import com.collegeroom.allocationsystem.model.BookingStatus;
import com.collegeroom.allocationsystem.model.User;
import com.collegeroom.allocationsystem.repository.BookingRepository;
import com.collegeroom.allocationsystem.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HodController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public HodController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentHod(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found: " + email));
    }

    @GetMapping("/hod/dashboard")
    public String hodDashboard(Model model, Authentication authentication) {
        User hod = getCurrentHod(authentication);

        List<Booking> pendingBookings = bookingRepository
                .findByStatusAndRequestedBy_Department(BookingStatus.PENDING, hod.getDepartment());

        model.addAttribute("hod", hod);
        model.addAttribute("pendingBookings", pendingBookings);
        return "hod-dashboard";
    }

    @PostMapping("/hod/bookings/{id}/approve")
    public String approveBooking(@PathVariable Long id, Authentication authentication) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));

        User hod = getCurrentHod(authentication);
        if (!booking.getRequestedBy().getDepartment().equals(hod.getDepartment())) {
            throw new SecurityException("Not authorized to act on this booking");
        }

        booking.setStatus(BookingStatus.HOD_APPROVED);
        bookingRepository.save(booking);
        return "redirect:/hod/dashboard";
    }

    @PostMapping("/hod/bookings/{id}/reject")
    public String rejectBooking(@PathVariable Long id, Authentication authentication) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));

        User hod = getCurrentHod(authentication);
        if (!booking.getRequestedBy().getDepartment().equals(hod.getDepartment())) {
            throw new SecurityException("Not authorized to act on this booking");
        }

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return "redirect:/hod/dashboard";
    }
}
