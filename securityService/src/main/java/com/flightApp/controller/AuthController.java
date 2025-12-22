package com.flightApp.controller;

import java.util.Optional;

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
import org.springframework.http.HttpHeaders;

import com.flightApp.dtos.ApiResponse;
import com.flightApp.dtos.AuthRequest;
import com.flightApp.dtos.AuthResponse;
import com.flightApp.dtos.ChangePasswordRequest;
import com.flightApp.dtos.RegisterRequest;
import com.flightApp.dtos.UpdateProfileRequest;
import com.flightApp.dtos.UserDto;
import com.flightApp.model.Role;
import com.flightApp.model.UserCredential;
import com.flightApp.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
            ResponseCookie jwtCookie = createJwtCookie(tokenData.getAccessToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(ApiResponse.success("Login Successful", tokenData));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        ResponseCookie cleanCookie = ResponseCookie.from("JWT_TOKEN", "") // Empty value
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) //Expire immediately
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(ApiResponse.success("Logged out successfully"));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token is valid"));
    }
    
    @GetMapping("/user/") 
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam("email") String email) {
        return service.getUserByEmail(email);
    }
    
    @GetMapping("/user/id/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        return service.getUserById(id);
    }
    

    @GetMapping("/user/userName") 
    public ResponseEntity<UserDto> getUserByUserName(@RequestParam("userName") String userName) {
        return service.getUserByUserName(userName);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        try {
            String result = service.changePassword(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/profile/update")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        try {
            UserDto updatedUser = service.updateProfile(request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }
    
    private ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from("JWT_TOKEN", token)
                .httpOnly(true)   // Secure: JS cannot read it
                .secure(false)    // Set to true in Prod (HTTPS)
                .path("/")        // Available everywhere
                .maxAge(30 * 60)  // 30 Minutes
                .sameSite("Lax")  // CSRF protection
                .build();
    }
}
