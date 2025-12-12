package com.flightApp.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.flightApp.model.UserCredential;

import java.util.Optional;

public interface UserCredentialRepository extends MongoRepository<UserCredential, String> {
    Optional<UserCredential> findByName(String name);
    Optional<UserCredential> findByEmail(String email);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
