package com.example.demo;

import com.example.demo.controller.UserController;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "John Doe", "john@example.com");
        user2 = new User(2L, "Jane Doe", "jane@example.com");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    public void testGetUserById() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void testCreateUser() throws Exception {
        User newUser = new User(null, "Alice", "alice@example.com");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(new User(3L, "Alice", "alice@example.com"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updatedUser = new User(1L, "John Smith", "john.smith@example.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Smith"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
