package com.flightApp.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.flightApp.model.User;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByEmailId(String emailId);
}
