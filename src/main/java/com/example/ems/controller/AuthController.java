package com.example.ems.controller;

import com.example.ems.model.AppUser;
import com.example.ems.repository.UserRepository;
import com.example.ems.security.JwtService;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository u, PasswordEncoder e, JwtService j) {
        users = u;
        encoder = e;
        jwt = j;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");
        Optional<AppUser> found = users.findByUsername(username);
        if (found.isEmpty() || !encoder.matches(password, found.get().getPasswordHash()))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        AppUser u = found.get();
        return ResponseEntity.ok(Map.of("token", jwt.generate(u.getUsername(), u.getRole()), "username",
                u.getUsername(), "role", u.getRole()));
    }
}
