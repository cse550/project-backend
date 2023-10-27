package com.cse550.projectbackend.user;

import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setUserId("testId");

        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("testId"));

        verify(userService, times(1)).saveUser(any(User.class));
    }


    @Test
    public void testDeleteUser() throws Exception {
        String existingUserId = "testId";

        doNothing().when(userService).deleteUser(existingUserId);

        mockMvc.perform(delete("/user/{id}", existingUserId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(existingUserId);
    }



    @Test
    public void testGetUser() throws Exception {
        User user = new User();
        user.setUserId("testId");

        when(userService.getUser("testId")).thenReturn(user);

        mockMvc.perform(get("/user/testId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("testId"));

        verify(userService, times(1)).getUser("testId");
    }

}
