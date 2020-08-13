package com.example.demo.models;

import com.example.demo.db.UserEntities;
import com.example.demo.db.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserModelTest {
    @Autowired
    private UserModel userModel;
    @MockBean
    UserRepository userRepository;

    @Test
    void listUsers() {
        UserEntities u1=this.generateUserEntities();
        UserEntities u2=this.generateUserEntities();
        Flux<UserEntities> entitiesFlux=Flux.just(u1,u2);
        Mockito.when(this.userRepository.findAll()).thenReturn(entitiesFlux);
        StepVerifier.create(this.userModel.listUsers()).expectNext(u1,u2).verifyComplete();
    }

    @Test
    void getUserById() {
        UserEntities userEntities = this.generateUserEntities();
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.just(userEntities));
        StepVerifier.create(this.userModel.getUserById("x"))
                .expectNext(userEntities)
                .verifyComplete();
    }

    @Test
    void addNewUser() {
        UserEntities userEntities = this.generateUserEntities();
        Mockito.when(this.userRepository.save(Mockito.any())).thenReturn(Mono.just(userEntities));
        StepVerifier.create(this.userModel.addNewUser(userEntities))
                .expectNext(userEntities).verifyComplete();

    }

    @Test
    void updateUserData() {
        UserEntities userEntities = this.generateUserEntities();
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.just(userEntities));
        Mockito.when(this.userRepository.save(userEntities)).thenReturn(Mono.just(userEntities));
        BDDMockito.when(this.userModel.updateUserData("x", userEntities))
                .thenReturn(Mono.just(userEntities));

        StepVerifier.create(this.userModel.updateUserData("x", userEntities))
                .expectNext(userEntities).verifyComplete();
    }

    @Test
    void deleteUser() {
        UserEntities userEntities = this.generateUserEntities();
        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.empty());
        StepVerifier.create(this.userModel.deleteUser("x"))
                .expectError().verify();

        Mockito.when(this.userRepository.findById(Mockito.anyString())).thenReturn(Mono.just(userEntities));
        Mockito.when(this.userRepository.deleteById(Mockito.anyString())).thenReturn(Mono.empty());
        StepVerifier.create(this.userModel.deleteUser(Mockito.anyString()))
                .expectNext("ok")
                .verifyComplete();
    }

    private UserEntities generateUserEntities() {
        UserEntities userEntities = new UserEntities();
        userEntities.setEmail("ax@gmail.com");
        userEntities.setId(UUID.randomUUID().toString());
        userEntities.setUsername("fahmi");
        return userEntities;
    }
}