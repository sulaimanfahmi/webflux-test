package com.example.demo.controllers;

import com.example.demo.db.UserEntities;
import com.example.demo.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private final UserModel userModel;

    public UserController(UserModel userModel) {
        this.userModel = userModel;
    }

    @GetMapping("/list")
    public Flux<UserEntities> getALlUser() {
        return this.userModel.listUsers();
    }

    @PostMapping("/add")
    public Mono<UserEntities> addNewUser(@RequestBody UserEntities userEntities) {
        return this.userModel.addNewUser(userEntities);
    }

    @GetMapping("/{id}/detail")
    public Mono<UserEntities> getUserDetaill(@PathVariable("id") String id) {
        return this.userModel.getUserById(id);
    }

    @PutMapping("/{id}/update")
    public Mono<ResponseEntity<UserEntities>> updateUserDetail(@PathVariable("id") String id, @RequestBody UserEntities payload) {
        return this.userModel.updateUserData(id, payload)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}/delete")
    public Mono<Void> deleteUser(@PathVariable("id") String id) {
        return this.userModel.deleteUser(id);
    }
}