package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.token.JwtTokenProvider;
import com.cse550.projectbackend.user.error.BadCredentialsException;
import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.CreateUserRequest;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public String createUser(CreateUserRequest createUserRequest) {
        try {
            User newUser = new User();
            newUser.setUsername(createUserRequest.getUsername());
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setPasswordHash(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
            newUser.setFollowing(new ArrayList<>());
            newUser.setCreatedAt(Instant.now());
            newUser.setId(UUID.randomUUID().toString());

            log.info("User created with id {} at {}", newUser.getId(), newUser.getCreatedAt());
            userRepository.save(newUser);
            return jwtTokenProvider.generateToken(newUser);
        } catch (DataAccessException e) {
            log.error("Error when saving user: ", e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public void deleteUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        User user = userOptional.get();
        try {
            log.info("Deleting User with id {}", user.getId());
            userRepository.delete(user);
            log.info("Successfully deleted User with id {}", user.getId());
        } catch (DataAccessException e) {
            log.error("Error when deleting user with id {}: ", userId, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find username of " + username));

        if (new BCryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            return jwtTokenProvider.generateToken(user);
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public User followUser(String userId, String followedUserId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        Optional<User> followedUserOptional = userRepository.findById(followedUserId);
        if (followedUserOptional.isEmpty()) {
            throw new UserNotFoundException("could not find user id of " + followedUserId);
        }

        User user = userOptional.get();
        User followedUser = followedUserOptional.get();

        if (!user.getFollowing().contains(followedUser)) {
            user.getFollowing().add(followedUser);

            try {
                userRepository.save(user);
                return user;
            } catch (DataAccessException e) {
                log.error("Error when following user with id {}: ", followedUserId, e);
                throw new RuntimeException("Failed to follow user", e);
            }
        }
        return user;
    }

    public User getUser(String identifier) {
        Optional<User> userOptional = userRepository.findByUsername(identifier);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findById(identifier);
        }

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Could not find user with identifier: " + identifier);
        }

        return userOptional.get();
    }

}
