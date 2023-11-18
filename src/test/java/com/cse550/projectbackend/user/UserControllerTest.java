package com.cse550.projectbackend.user;

import com.cse550.projectbackend.user.model.CreateUserRequest;
import com.cse550.projectbackend.user.model.LoginRequest;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId("test");
        testUser.setUsername("test");
        testUser.setPasswordHash("password");
    }

    @Test
    public void testCreateUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUser);

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test", Objects.requireNonNull(response.getBody()).getId());

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    public void testDeleteUser() {

        doNothing().when(userService).deleteUser(testUser.getId());

        ResponseEntity<Void> response = userController.deleteUser(testUser.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(userService, times(1)).deleteUser(testUser.getId());
    }

    @Test
    public void testFollowUser() {

        when(userService.followUser(anyString(), anyString())).thenReturn(testUser);

        ResponseEntity<User> response = userController.followUser("test", "followedUserId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", Objects.requireNonNull(response.getBody()).getId());
        verify(userService, times(1)).followUser("test", "followedUserId");
    }

    @Test
    public void testLoginUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        String mockToken = "mockJwtToken";
        when(userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword())).thenReturn(mockToken);

        ResponseEntity<?> response = userController.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService, times(1)).loginUser(loginRequest.getUsername(), loginRequest.getPassword());
    }
}
