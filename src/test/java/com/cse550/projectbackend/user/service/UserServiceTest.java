package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.token.JwtTokenProvider;
import com.cse550.projectbackend.user.error.BadCredentialsException;
import com.cse550.projectbackend.user.error.UserNotFoundException;
import com.cse550.projectbackend.user.model.CreateUserRequest;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.model.UserDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private UserDTO userDTO;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testUser = createUser("1", "user1", "user1@example.com");
        testUser.setFollowing(new ArrayList<>());

        userDTO = UserDTO.builder()
                .username("updatedUsername")
                .email("updatedEmail@example.com")
                .following(Arrays.asList("2", "3"))
                .bio("this is a bio")
                .build();

        createUserRequest = new CreateUserRequest();
        createUserRequest.setPassword("testPassword");
        createUserRequest.setUsername("testman");
        createUserRequest.setEmail("testman@ILoveUnitTesting.com");
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("mockedToken");

        String token = userService.createUser(createUserRequest);

        assertNotNull(token);
        assertEquals("mockedToken", token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithException() {
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("Error") {});

        assertThrows(RuntimeException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.deleteUser("1"));
        verify(userRepository).delete(testUser);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("1"));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testLoginUserWithValidPassword() {
        String password = "testPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        testUser.setPasswordHash(encodedPassword);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("mockedToken");

        String token = userService.loginUser("user1", password);

        assertNotNull(token);
        assertEquals("mockedToken", token);
    }

    @Test
    void testLoginUserWithInvalidPassword() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        testUser.setPasswordHash(new BCryptPasswordEncoder().encode("testPassword"));

        assertThrows(BadCredentialsException.class, () -> userService.loginUser("user1", "wrongPassword"));
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.loginUser("user1", "testPassword"));
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("mockedToken");

        String token = userService.updateUser("1", userDTO);

        assertNotNull(token);
        assertEquals("mockedToken", token);
        verify(userRepository).save(testUser);
        assertTrue(testUser.getFollowing().containsAll(Arrays.asList("2", "3")));
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser("1", userDTO));
    }
    @Test
    void getUsersWhenMoreThan10UsersReturnsFirst10Users() {
        List<User> mockUsers = IntStream.range(0, 15)
                .mapToObj(i -> createUser(String.valueOf(i), "User" + i, "email" + i + "@example.com"))
                .collect(Collectors.toList());
        when(userRepository.findAll()).thenReturn(mockUsers);
        List<User> users = userService.getUsers();

        assertEquals(10, users.size());
        for (int i = 0; i < 10; i++) {
            assertEquals("User" + i, users.get(i).getUsername());
        }
    }

    @Test
    void getUsersWhenLessThan10UsersReturnsAllUsers() {
        List<User> mockUsers = IntStream.range(0, 5)
                .mapToObj(i -> createUser(String.valueOf(i), "User" + i, "email" + i + "@example.com"))
                .collect(Collectors.toList());
        when(userRepository.findAll()).thenReturn(mockUsers);
        List<User> users = userService.getUsers();
        assertEquals(5, users.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("User" + i, users.get(i).getUsername());
        }
    }
    @Test
    void testGetUserOrThrowFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.getUser("1"));
    }

    @Test
    void testGetUserOrThrowNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser("1"));
    }

    private User createUser(String id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
