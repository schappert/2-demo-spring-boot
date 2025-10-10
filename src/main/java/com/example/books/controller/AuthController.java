package com.example.books.controller;

import com.example.books.dto.AuthRequest;
import com.example.books.dto.AuthResponse;
import com.example.books.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse resp = authService.loginResponse(request);
        return ResponseEntity.ok(resp);
    }

    // POST /api/auth/register (optionnel)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse resp = authService.register(request.username(), request.password());
        return ResponseEntity.ok(resp);
    }
}
