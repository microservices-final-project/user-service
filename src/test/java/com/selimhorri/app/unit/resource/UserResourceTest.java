package com.selimhorri.app.unit.resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.resource.UserResource;
import com.selimhorri.app.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserResource userResource;
    
    private UserDto userDto;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
        objectMapper = new ObjectMapper();
        
        userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPhone("1234567890");
    }
    
    @Test
    void findAll_shouldReturnUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto));
        
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].userId").value(1));
        
        verify(userService, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnUserWhenFound() throws Exception {
        when(userService.findById(anyInt())).thenReturn(userDto);
        
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userService, times(1)).findById(1);
    }

    @Test
    void findByUsername_shouldReturnUserWhenFound() throws Exception {
        when(userService.findByUsername(anyString())).thenReturn(userDto);
        
        mockMvc.perform(get("/api/users/username/johndoe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userService, times(1)).findByUsername("johndoe");
    }
    
    @Test
    void save_shouldSaveUser() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(userDto);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userService, times(1)).save(any(UserDto.class));
    }
    
    @Test
    void update_shouldUpdateUser() throws Exception {
        when(userService.update(any(UserDto.class))).thenReturn(userDto);
        
        mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userService, times(1)).update(any(UserDto.class));
    }
    
    @Test
    void updateWithId_shouldUpdateUser() throws Exception {
        when(userService.update(anyInt(), any(UserDto.class))).thenReturn(userDto);
        
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userService, times(1)).update(eq(1), any(UserDto.class));
    }
    
    @Test
    void deleteById_shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteById(anyInt());
        
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(userService, times(1)).deleteById(1);
    }
    

}