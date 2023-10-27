package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testFollowedUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("1");
        testUser.setUsername("user1");
        testUser.setEmail("user1@example.com");

        testFollowedUser = new User();
        testFollowedUser.setUserId("2");
        testFollowedUser.setUsername("user2");
        testFollowedUser.setEmail("user2@example.com");
    }

    @Test
    void testSaveUser() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser.getUserId()); // since a UUID is set on saving
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser_existingUser() {
        User user = new User();
        user.setUserId("testId");

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        userService.deleteUser("testId");

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
        user.setUserId("testId");

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        User foundUser = userService.getUser("testId");

        assertEquals("testId", foundUser.getUserId());
        verify(userRepository, times(1)).findById("testId");
    }

    @Test
    void testGetUser_userNotFound() {
        when(userRepository.findById("testId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUser("testId"));
        assertEquals("User with id testId not found", exception.getMessage());

        verify(userRepository, times(1)).findById("testId");
    }

    @Test
    public void testFollowUser() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.findById("2")).thenReturn(Optional.of(testFollowedUser));

        List<User> initialFollowingList = new ArrayList<>();
        testUser.setFollowing(initialFollowingList);

        User followedUser = userService.followUser("1", "2");

        assertNotNull(followedUser);
        assertEquals(1, followedUser.getFollowing().size());
        assertTrue(followedUser.getFollowing().contains(testFollowedUser));
    }


    @Test
    public void testFollowUserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.followUser("1", "2"));
    }

    @Test
    public void testFollowUserAlreadyFollowing() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.findById("2")).thenReturn(Optional.of(testFollowedUser));

        List<User> initialFollowingList = new ArrayList<>();
        initialFollowingList.add(testFollowedUser);
        testUser.setFollowing(initialFollowingList);

        User followedUser = userService.followUser("1", "2");

        assertEquals(1, followedUser.getFollowing().size());
        assertTrue(followedUser.getFollowing().contains(testFollowedUser));
    }

}
