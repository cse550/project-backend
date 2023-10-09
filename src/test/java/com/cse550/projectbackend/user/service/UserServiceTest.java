package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser.getUserID()); // since a UUID is set on saving
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser_existingUser() {
        User user = new User();
        user.setUserID("testId");

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        User deletedUser = userService.deleteUser("testId");

        assertEquals("testId", deletedUser.getUserID());
        verify(userRepository, times(1)).findById("testId");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_userNotFound() {
        when(userRepository.findById("testId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser("testId"));
        assertEquals("User with id testId not found", exception.getMessage());

        verify(userRepository, times(1)).findById("testId");
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testGetUser_existingUser() {
        User user = new User();
        user.setUserID("testId");

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        User foundUser = userService.getUser("testId");

        assertEquals("testId", foundUser.getUserID());
        verify(userRepository, times(1)).findById("testId");
    }

    @Test
    void testGetUser_userNotFound() {
        when(userRepository.findById("testId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUser("testId"));
        assertEquals("User with id testId not found", exception.getMessage());

        verify(userRepository, times(1)).findById("testId");
    }
}
