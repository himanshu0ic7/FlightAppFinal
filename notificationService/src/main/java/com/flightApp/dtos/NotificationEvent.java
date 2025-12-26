package com.flightApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationEvent {
	private String mobileNumber;
    private String message;
    private String type;
    private String email;
	
}
