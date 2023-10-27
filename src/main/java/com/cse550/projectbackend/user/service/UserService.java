package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user) {
        try {
            user.setCreatedAt(Instant.now());
            user.setUserID(UUID.randomUUID().toString());
            log.info("User created with id {} at {}", user.getUserID(), user.getCreatedAt());
            return userRepository.save(user);
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
            log.info("Deleting User with id {}", user.getUserID());
            userRepository.delete(user);
            log.info("Successfully deleted User with id {}", user.getUserID());
        } catch (DataAccessException e) {
            log.error("Error when deleting user with id {}: ", userId, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public User getUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        return userOptional.get();
    }

    public User followUser(String userId, String followedUserId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        Optional<User> followedUserOptional = userRepository.findById(followedUserId);
        if (followedUserOptional.isEmpty()) {
            log.error("User with id {} not found", followedUserId);
            throw new UserNotFoundException(followedUserId);
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

}
