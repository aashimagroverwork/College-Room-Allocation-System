package com.collegeroom.allocationsystem;

import com.collegeroom.allocationsystem.model.Room;
import com.collegeroom.allocationsystem.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoomDataSeeder implements CommandLineRunner {

    private final RoomRepository roomRepository;

    public RoomDataSeeder(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println(">>> Starting Room Data Seeder...");

        // 1. Seed Lrs 1 to 50 (Capacity 70)
        for (int i = 1; i <= 50; i++) {
            String name = "Lrs " + i;
            if (roomRepository.findByName(name).isEmpty()) {
                Room r = new Room();
                r.setName(name);
                r.setCapacity(70);
                r.setBuilding("Lrs Block");
                roomRepository.save(r);
            }
        }

        // 2. Seed Crs 1 to 40 (Capacity 50)
        for (int i = 1; i <= 40; i++) {
            String name = "Crs " + i;
            if (roomRepository.findByName(name).isEmpty()) {
                Room r = new Room();
                r.setName(name);
                r.setCapacity(50);
                r.setBuilding("Crs Block");
                roomRepository.save(r);
            }
        }

        // 3. Seed other specialized rooms
        // Rename old misspelled room if it exists in the database
        roomRepository.findByName("Bonut lab").ifPresent(r -> {
            r.setName("Bonet lab");
            roomRepository.save(r);
        });

        // Delete obsolete rooms if they exist
        String[] roomsToDelete = {"Room 101", "Seminar Hall 1", "Lab 203"};
        for (String rName : roomsToDelete) {
            roomRepository.findByName(rName).ifPresent(r -> {
                try {
                    roomRepository.delete(r);
                    System.out.println(">>> Deleted obsolete room: " + rName);
                } catch (Exception e) {
                    System.out.println(">>> Could not delete obsolete room " + rName + ": " + e.getMessage());
                }
            });
        }

        String[] individualRooms = {"Seminar room", "Mmr", "Scavi", "Bonet lab", "Knowledge lab"};
        int[] capacities = {80, 90, 60, 80, 30};
        String[] buildings = {"Main Block", "Main Block", "Main Block", "Science Block", "Library Block"};

        for (int i = 0; i < individualRooms.length; i++) {
            String name = individualRooms[i];
            if (roomRepository.findByName(name).isEmpty()) {
                Room r = new Room();
                r.setName(name);
                r.setCapacity(capacities[i]);
                r.setBuilding(buildings[i]);
                roomRepository.save(r);
            }
        }

        System.out.println(">>> Room Data Seeder completed successfully!");
    }
}
