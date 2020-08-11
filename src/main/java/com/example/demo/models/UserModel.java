package com.example.demo.models;

import com.example.demo.db.UserEntities;
import com.example.demo.db.UserRepository;
import com.example.demo.errorHandler.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserModel {

    @Autowired
    private final UserRepository userRepository;

    public UserModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<UserEntities> listUsers() {
        return this.userRepository.findAll();
    }

    public Mono<UserEntities> getUserById(String userId) {
        return this.userRepository.findById(userId);
    }

    public Mono<UserEntities> addNewUser(UserEntities userEntities) {
        return this.userRepository.save(userEntities);
    }

    public Mono<UserEntities> updateUserData(String id, UserEntities userEntities) {
        return this.userRepository.findById(id)
                .flatMap(data -> {
                    data.setEmail(userEntities.getEmail());
                    data.setUsername(userEntities.getUsername());
                    return this.userRepository.save(data);
                });
    }

    public Mono<Void> deleteUser(String id) {
        return this.userRepository.deleteById(id);
    }
}
