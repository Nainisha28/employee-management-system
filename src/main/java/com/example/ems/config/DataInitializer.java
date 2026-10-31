package com.example.ems.config;

import com.example.ems.model.AppUser;
import com.example.ems.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {

            if (users.findByUsername("admin").isEmpty()) {
                users.save(new AppUser(
                        "admin",
                        encoder.encode("Admin@12345"),
                        "ADMIN"
                ));
            }

            if (users.findByUsername("manager").isEmpty()) {
                users.save(new AppUser(
                        "manager",
                        encoder.encode("Manager@12345"),
                        "MANAGER"
                ));
            }

            if (users.findByUsername("employee").isEmpty()) {
                users.save(new AppUser(
                        "employee",
                        encoder.encode("Employee@12345"),
                        "EMPLOYEE"
                ));
            }
        };
    }
}