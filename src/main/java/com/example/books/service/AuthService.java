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

    /**
     * Vérifie credentials, lance une ResponseStatusException(401) en cas d'échec.
     */
    public void authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    /**
     * Retourne le token JWT (chaîne) après authentification.
     */
    public String login(String username, String password) {
        authenticate(username, password);
        return jwtUtil.generateToken(username);
    }

    /**
     * Retourne un AuthResponse (token) pour faciliter le controller.
     */
    public AuthResponse loginResponse(AuthRequest request) {
        String token = login(request.username(), request.password());
        return new AuthResponse(token);
    }

    /**
     * Méthode d'inscription minimale (encode password, save).
     * Tu peux ajouter validation / unique checks etc.
     */
    public AuthResponse register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        String token = jwtUtil.generateToken(username);
        return new AuthResponse(token);
    }
}
