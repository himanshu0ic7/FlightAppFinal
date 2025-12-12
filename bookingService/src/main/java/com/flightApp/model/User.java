package com.flightApp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="users")
public class User{

    @Id
    String id;

    @NotBlank(message = "Name cannot be empty")
    String name;
    

    @Indexed(unique=true)
    @Email(message = "Invalid email format")
    String emailId;
    
    @NotBlank(message = "Mobile number is required")
    String mobileNumber;
    
}
