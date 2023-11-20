package com.cse550.projectbackend.user;

import com.cse550.projectbackend.token.JwtResponse;
import com.cse550.projectbackend.user.error.BadCredentialsException;
import com.cse550.projectbackend.user.error.UserNotFoundException;
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
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest createUserRequest) {
        String token = userService.createUser(createUserRequest);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
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
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException | UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

}