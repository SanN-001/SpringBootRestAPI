package com.example.restapi;

import com.example.restapi.controller.UserController;
import com.example.restapi.model.User;
import com.example.restapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[1].name", is("Jane")));
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John\", \"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("john.doe@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\", \"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}