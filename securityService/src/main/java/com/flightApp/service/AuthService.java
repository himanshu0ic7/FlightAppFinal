package com.flightApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flightApp.dtos.UserDto;
import com.flightApp.dtos.AuthResponse;
import com.flightApp.dtos.ChangePasswordRequest;
import com.flightApp.dtos.RegisterRequest;
import com.flightApp.dtos.UpdateProfileRequest;
import com.flightApp.model.Role;
import com.flightApp.model.UserCredential;
import com.flightApp.repo.UserCredentialRepository;

import jakarta.validation.Valid;

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
                .fullname(request.getFullname())
                .mobileNumber(request.getMobileNumber())
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

	public ResponseEntity<UserDto> getUserByEmail(String email) {
		return repository.findByEmail(email)
        .map(user -> ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build()))
        .orElse(ResponseEntity.notFound().build());
	}

	public ResponseEntity<UserDto> getUserById(String id) {
		return repository.findById(id)
		        .map(user -> ResponseEntity.ok(UserDto.builder()
		                .id(user.getId())
		                .name(user.getName())
		                .fullname(user.getFullname())
		                .email(user.getEmail())
		                .mobileNumber(user.getMobileNumber())
		                .build()))
		        .orElse(ResponseEntity.notFound().build());
	}

	public ResponseEntity<UserDto> getUserByUserName(String userName) {
		return repository.findByName(userName)
		        .map(user -> ResponseEntity.ok(UserDto.builder()
		                .id(user.getId())
		                .name(user.getName())
		                .fullname(user.getFullname())
		                .email(user.getEmail())
		                .mobileNumber(user.getMobileNumber())
		                .build()))
		        .orElse(ResponseEntity.notFound().build());
	}

	public String changePassword(@Valid ChangePasswordRequest request) {
		UserCredential user = repository.findByName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        repository.save(user);

        return "Password changed successfully";
	}

	public UserDto updateProfile(@Valid UpdateProfileRequest request) {
		UserCredential user = repository.findByName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        
        UserCredential updatedUser = repository.save(user);

        return new UserDto(
            updatedUser.getId(),
            updatedUser.getFullname(),
            updatedUser.getName(),
            updatedUser.getEmail(),
            updatedUser.getMobileNumber()
        );
	}
}
