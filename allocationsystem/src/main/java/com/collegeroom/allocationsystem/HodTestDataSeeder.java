package com.collegeroom.allocationsystem;

import com.collegeroom.allocationsystem.model.*;
import com.collegeroom.allocationsystem.repository.BookingRepository;
import com.collegeroom.allocationsystem.repository.RoomRepository;
import com.collegeroom.allocationsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class HodTestDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public HodTestDataSeeder(UserRepository userRepository, RoomRepository roomRepository,
                              BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (userRepository.findByEmail("hod@test.com").isEmpty()) {
                User hod = new User();
                hod.setName("Test HOD");
                hod.setEmail("hod@test.com");
                hod.setPassword(passwordEncoder.encode("password123"));
                hod.setRole(Role.HOD);
                hod.setDepartment("Computer Science");
                userRepository.save(hod);
                System.out.println(">>> Test HOD created: hod@test.com / password123");
            }

            if (userRepository.findByEmail("student@test.com").isEmpty()) {
                User student = new User();
                student.setName("Test Student");
                student.setEmail("student@test.com");
                student.setPassword(passwordEncoder.encode("password123"));
                student.setRole(Role.STUDENT);
                student.setDepartment("Computer Science");
                userRepository.save(student);

                Room room = roomRepository.findByName("Lecture Hall 1")
                        .orElseGet(() -> {
                            Room newRoom = new Room();
                            newRoom.setName("Lecture Hall 1");
                            newRoom.setCapacity(60);
                            newRoom.setBuilding("Main Block");
                            return roomRepository.save(newRoom);
                        });

                Booking booking = new Booking();
                booking.setRoom(room);
                booking.setRequestedBy(student);
                booking.setDate(LocalDate.now().plusDays(1));
                booking.setStartTime(LocalTime.of(10, 0));
                booking.setEndTime(LocalTime.of(11, 0));
                booking.setPurpose("Guest lecture");
                bookingRepository.save(booking);

                System.out.println(">>> Test student + pending booking created for HOD testing");
            }
        } catch (Throwable t) {
            System.err.println(">>> Non-fatal error during HodTestDataSeeder: " + t.getMessage());
        }
    }
}
