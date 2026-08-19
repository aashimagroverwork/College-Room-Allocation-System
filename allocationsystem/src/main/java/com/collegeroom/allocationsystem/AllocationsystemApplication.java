package com.collegeroom.allocationsystem;

import com.collegeroom.allocationsystem.model.Role;
import com.collegeroom.allocationsystem.model.User;
import com.collegeroom.allocationsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AllocationsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllocationsystemApplication.class, args);
    }

    @Bean
    CommandLineRunner createTestUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@test.com").isEmpty()) {
                User admin = new User();
                admin.setName("Test Admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println(">>> Test admin user created: admin@test.com / password123");
            }
        };
    }
}