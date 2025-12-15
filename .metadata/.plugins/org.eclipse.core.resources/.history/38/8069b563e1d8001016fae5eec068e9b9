package com.flightApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flightApp.dtos.AuthResponse;
import com.flightApp.dtos.RegisterRequest;
import com.flightApp.model.Role;
import com.flightApp.model.UserCredential;
import com.flightApp.repo.UserCredentialRepository;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public String saveUser(RegisterRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Username '" + request.getName() + "' is already taken.");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        UserCredential user = UserCredential.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.ROLE_USER)
                .build();
                
        repository.save(user);
        return "User added to the system";
    }

    public AuthResponse generateToken(String username) {
        UserCredential user = repository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        String token = jwtService.generateToken(username, user.getRole().name());
        
        return AuthResponse.builder()
                .accessToken(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
    
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
