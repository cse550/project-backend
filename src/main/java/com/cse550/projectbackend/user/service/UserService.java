package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.token.JwtTokenProvider;
import com.cse550.projectbackend.user.error.BadCredentialsException;
import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.CreateUserRequest;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.model.UserDTO;
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
            newUser.setId(UUID.randomUUID().toString());
            newUser.setUsername(createUserRequest.getUsername());
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setPasswordHash(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
            newUser.setFollowing(new ArrayList<>());
            newUser.getFollowing().add(newUser.getId());
            newUser.setCreatedAt(Instant.now());

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
        User user = getUserOrThrow(username);

        if (new BCryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            return jwtTokenProvider.generateToken(user);
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public User getUser(String identifier) {
        return getUserOrThrow(identifier);
    }

    public String updateUser(String userId, UserDTO userDTO) {
        try {
            User existingUser = getUserOrThrow(userId);

            if (userDTO.getUsername() != null) {
                existingUser.setUsername(userDTO.getUsername());
            }
            if (userDTO.getEmail() != null) {
                existingUser.setEmail(userDTO.getEmail());
            }
            if (userDTO.getPasswordHash() != null) {
                existingUser.setPasswordHash(new BCryptPasswordEncoder().encode(userDTO.getPasswordHash()));
            }
            if (userDTO.getFollowing() != null) {
                for (String followingId : userDTO.getFollowing()) {
                    if (!existingUser.getFollowing().contains(followingId)) {
                        existingUser.getFollowing().add(followingId);
                    }
                }
            }
            if (userDTO.getBio() != null) {
                existingUser.setBio(userDTO.getBio());
            }

            userRepository.save(existingUser);
            log.info("User updated with id {}", existingUser.getId());

            return jwtTokenProvider.generateToken(existingUser);
        } catch (DataAccessException e) {
            log.error("Error when updating user: ", e);
            throw new UserNotFoundException("could not find user id of " + userId);
        }
    }

    private User getUserOrThrow(String identifier) {
        return userRepository.findById(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> {
                    log.error("User with identifier {} not found", identifier);
                    return new UserNotFoundException("Could not find user with identifier: " + identifier);
                });
    }
}
