package com.example.books.service;

import com.example.books.dto.AuthRequest;
import com.example.books.dto.AuthResponse;
import com.example.books.model.User;
import com.example.books.repository.UserRepository;
import com.example.books.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    public AuthResponse loginResponse(AuthRequest request) {
        authenticate(request.username(), request.password());
        String access = jwtUtil.generateAccessToken(request.username());
        String refresh = jwtUtil.generateRefreshToken(request.username());

        User user = userRepository.findByUsername(request.username()).orElseThrow();
        user.setRefreshToken(refresh);
        userRepository.save(user);

        return new AuthResponse(access, refresh);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token expired");

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!refreshToken.equals(user.getRefreshToken()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token invalidated");

        String newAccess = jwtUtil.generateAccessToken(username);
        return new AuthResponse(newAccess, refreshToken);
    }

    public AuthResponse register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        String refresh = jwtUtil.generateRefreshToken(username);
        user.setRefreshToken(refresh);
        userRepository.save(user);

        String access = jwtUtil.generateAccessToken(username);
        return new AuthResponse(access, refresh);
    }
}
