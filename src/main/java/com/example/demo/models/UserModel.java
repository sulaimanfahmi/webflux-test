package com.example.demo.models;

import com.example.demo.db.UserEntities;
import com.example.demo.db.UserRepository;

import com.example.demo.errorHandler.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class UserModel {

    @Autowired
    private final UserRepository userRepository;

    public Flux<UserEntities> streamUsers() {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        Flux<UserEntities> entitiesFlux = Flux.from(this.userRepository.findAll());
        return Flux.zip(entitiesFlux, interval, (key, value) -> key);
    }

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

    public Mono<String> deleteUser(String id) {
        return this.userRepository.findById(id).flatMap(userEntities -> {
            return this.userRepository.deleteById(userEntities.getId()).thenReturn("ok");
        }).switchIfEmpty(Mono.error(new NotFoundException("user not found")));
    }
}
