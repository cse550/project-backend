package com.cse550.projectbackend.user;

import com.cse550.projectbackend.user.model.CreateUserRequest;
import com.cse550.projectbackend.user.model.LoginRequest;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User newUser = userService.createUser(createUserRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/follow/{followedUserId}")
    public ResponseEntity<User> followUser(
            @PathVariable String userId,
            @PathVariable String followedUserId
    ) {
        User user = userService.followUser(userId, followedUserId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginRequest loginRequest) {
        User user = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}