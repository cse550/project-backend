package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.token.JwtTokenProvider;
import com.cse550.projectbackend.user.error.BadCredentialsException;
import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.CreateUserRequest;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testFollowedUser;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("user1");
        testUser.setEmail("user1@example.com");

        testFollowedUser = new User();
        testFollowedUser.setId("2");
        testFollowedUser.setUsername("user2");
        testFollowedUser.setEmail("user2@example.com");

        createUserRequest = new CreateUserRequest();
        createUserRequest.setPassword("testPassword");
        createUserRequest.setUsername("testman");
        createUserRequest.setEmail("testman@ILoveUnitTesting.com");
    }

    @Test
    void testDeleteUserExistingUser() {

        when(userRepository.findById("testId")).thenReturn(Optional.of(testUser));

        userService.deleteUser("testId");

        verify(userRepository, times(1)).findById("testId");
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUserUserNotFound() {
        when(userRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("testId"));

        verify(userRepository, times(1)).findById("testId");
        verify(userRepository, never()).delete(any(User.class));
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

    @Test
    void testSaveUserWithDataAccessException() {
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("Error") {});
        assertThrows(RuntimeException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUserWithDataAccessException() {
        when(userRepository.findById("testId")).thenReturn(Optional.of(new User()));
        doThrow(new DataAccessException("Error") {}).when(userRepository).delete(any(User.class));
        assertThrows(RuntimeException.class, () -> userService.deleteUser("testId"));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testFollowUserWithDataAccessException() {
        testUser.setFollowing(new ArrayList<>());
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.findById("2")).thenReturn(Optional.of(testFollowedUser));
        doThrow(new DataAccessException("Error") {}).when(userRepository).save(any(User.class));
        assertThrows(RuntimeException.class, () -> userService.followUser("1", "2"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginUserWithValidPassword() {
        String password = "testPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        testUser.setPasswordHash(encodedPassword);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("mockedJwtToken");

        String token = userService.loginUser(testUser.getUsername(), password);

        assertNotNull(token);

    }


    @Test
    void testLoginUserWithInvalidPasswordThrowsBadCredentialsException() {
        String validPassword = "testPassword";
        String invalidPassword = "wrongPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(validPassword);

        testUser.setPasswordHash(encodedPassword);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(BadCredentialsException.class, () -> {
            userService.loginUser(testUser.getUsername(), invalidPassword);
        });
    }

}
