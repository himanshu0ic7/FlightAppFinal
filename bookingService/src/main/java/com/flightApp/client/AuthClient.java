package com.flightApp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.flightApp.dtos.RegisterRequest;
import com.flightApp.dtos.UserDto;

@FeignClient(name = "securityService")
public interface AuthClient {
	@GetMapping("/auth/user/")
    UserDto getUserByEmail(@RequestParam("email") String email);

    @PostMapping("/auth/register")
    String register(@RequestBody RegisterRequest request);
    
    @GetMapping("/auth/user/id/{id}")
    UserDto getUserById(@PathVariable String id);
    
    @GetMapping("/auth/user/userName") 
    UserDto getUserByUserName(@RequestParam("userName") String userName);
}
