package com.collegeroom.allocationsystem.repository;

import com.collegeroom.allocationsystem.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}