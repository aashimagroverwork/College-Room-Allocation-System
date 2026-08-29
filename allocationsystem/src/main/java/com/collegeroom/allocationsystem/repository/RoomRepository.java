package com.collegeroom.allocationsystem.repository;

import com.collegeroom.allocationsystem.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String name);
}