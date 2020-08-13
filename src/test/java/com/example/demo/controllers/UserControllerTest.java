package com.example.demo.controllers;

import com.example.demo.db.UserEntities;
import com.example.demo.db.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;


    @Test
    void getALlUser() {
        Flux<UserEntities> flux = Flux.just(this.generateUserEntities(), this.generateUserEntities());
        Mockito.when(this.userRepository.findAll()).thenReturn(flux);
        this.webTestClient.get().uri("/users/list").exchange().expectStatus().is2xxSuccessful();
    }

    @Test
    void addNewUser() {
        UserEntities userEntities = this.generateUserEntities();
        Mono<UserEntities> monoUserEntities = Mono.just(userEntities);
        Mockito.when(this.userRepository.save(Mockito.any())).thenReturn(monoUserEntities);
        this.webTestClient.post()
                .uri("/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userEntities), UserEntities.class)
                .exchange().expectStatus().is2xxSuccessful().expectBody(UserEntities.class).value(userEntities1 -> {
            Assert.assertEquals(userEntities.getId(), userEntities1.getId());
        });
    }

    @Test
    void getUserDetaill() {
        UserEntities userEntities = this.generateUserEntities();
        Mono<UserEntities> monoUserEntities = Mono.just(userEntities);
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(monoUserEntities);
        this.webTestClient.get().uri("/users/x/detail").exchange().expectStatus().is2xxSuccessful()
                .expectBody(UserEntities.class).value(userEntities1 -> {
            Assert.assertEquals(userEntities.getId(), userEntities1.getId());
        });
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.empty());
        this.webTestClient.get().uri("/users/x/detail").exchange().expectStatus().is4xxClientError();
    }

    @Test
    void updateUserDetail() {
        UserEntities userEntities = this.generateUserEntities();
        Mono<UserEntities> monoUserEntities = Mono.just(userEntities);
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(monoUserEntities);
        userEntities.setUsername("ajo");
        Mockito.when(this.userRepository.save(Mockito.any())).thenReturn(Mono.just(userEntities));

        this.webTestClient.put()
                .uri("/users/x/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userEntities), UserEntities.class)
                .exchange().expectStatus().is2xxSuccessful().expectBody(UserEntities.class).value(userEntities1 -> {
            Assert.assertEquals(userEntities.getUsername(), "ajo");
        });
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.empty());
        this.webTestClient.get().uri("/users/x/update").exchange().expectStatus().is4xxClientError();
    }

    @Test
    void deleteUser() {
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.empty());
        this.webTestClient.delete().uri("/users/x/delete").exchange().expectStatus().is4xxClientError();
    }


    private UserEntities generateUserEntities() {
        UserEntities userEntities = new UserEntities();
        userEntities.setEmail("ax@gmail.com");
        userEntities.setId(UUID.randomUUID().toString());
        userEntities.setUsername("fahmi");
        return userEntities;
    }
}