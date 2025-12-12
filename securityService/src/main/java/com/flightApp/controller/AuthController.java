package com.flightApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flightApp.dtos.ApiResponse;
import com.flightApp.dtos.AuthRequest;
import com.flightApp.dtos.AuthResponse;
import com.flightApp.dtos.RegisterRequest;
import com.flightApp.model.Role;
import com.flightApp.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${flight.app.admin-secret}")
    private String adminSecret;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> addNewUser(
            @RequestBody @Valid RegisterRequest request,
            @RequestHeader(value = "X-Admin-Secret", required = false) String adminKey
    ) {
        if (request.getRole() == Role.ROLE_ADMIN) {
            if (adminKey == null || !adminKey.equals(adminSecret)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access Denied: unauthorized admin creation attempt"));
            }
        }

        String resultMessage = service.saveUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(resultMessage));
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthResponse>> getToken(@RequestBody @Valid AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authenticate.isAuthenticated()) {
            AuthResponse tokenData = service.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login Successful", tokenData));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token is valid"));
    }
}
