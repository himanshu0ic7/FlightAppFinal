package com.flightApp.dtos;

import com.flightApp.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	
	@NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String name;
	
	@NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "FullName must be between 3 and 30 characters")
    private String fullname;
	
	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character (@#$%^&+=)")
    private String password;
    
	private Role role;
}