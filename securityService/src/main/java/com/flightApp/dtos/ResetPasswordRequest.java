package com.flightApp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
	@NotBlank(message = "Username is required")
	private String username;
	@NotBlank(message = "otp is required")
    private String otp;
	@NotBlank(message = "newPassword is required")
	@Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;
}
