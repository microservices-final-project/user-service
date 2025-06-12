package com.selimhorri.app.resource;

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
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.exception.handler.GlobalExceptionHandler;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.exception.wrapper.UsernameAlreadyExistsException;
import com.selimhorri.app.service.CredentialService;

@ExtendWith(MockitoExtension.class)
public class CredentialResourceTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @Mock
    private CredentialService credentialService;
    
    @InjectMocks
    private CredentialResource credentialResource;
    
    private CredentialDto credentialDto;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(credentialResource)
                .setControllerAdvice(new GlobalExceptionHandler()) // Si tienes un manejador global
                .build();
        objectMapper = new ObjectMapper();
        
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        
        credentialDto = new CredentialDto();
        credentialDto.setCredentialId(1);
        credentialDto.setUsername("johndoe");
        credentialDto.setPassword("encodedPassword");
        credentialDto.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        credentialDto.setIsEnabled(true);
        credentialDto.setIsAccountNonExpired(true);
        credentialDto.setIsAccountNonLocked(true);
        credentialDto.setIsCredentialsNonExpired(true);
        credentialDto.setUserDto(userDto);
    }
    
    @Test
    void findAll_shouldReturnAllCredentials() throws Exception {
        when(credentialService.findAll()).thenReturn(List.of(credentialDto));
        
        mockMvc.perform(get("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].credentialId").value(1))
                .andExpect(jsonPath("$.collection[0].username").value("johndoe"));
        
        verify(credentialService, times(1)).findAll();
    }
    
    @Test
    void findByUsername_shouldReturnCredentialWhenFound() throws Exception {
        when(credentialService.findByUsername(anyString())).thenReturn(credentialDto);
        
        mockMvc.perform(get("/api/credentials/username/johndoe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"));
        
        verify(credentialService, times(1)).findByUsername("johndoe");
    }
    
    @Test
    void findByUsername_shouldReturn404WhenNotFound() throws Exception {
        when(credentialService.findByUsername(anyString()))
            .thenThrow(new UserObjectNotFoundException("Credential not found"));
        
        mockMvc.perform(get("/api/credentials/username/unknown")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(credentialService, times(1)).findByUsername("unknown");
    }
    
    @Test
    void findById_shouldReturnCredentialWhenFound() throws Exception {
        when(credentialService.findById(anyInt())).thenReturn(credentialDto);
        
        mockMvc.perform(get("/api/credentials/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
        
        verify(credentialService, times(1)).findById(1);
    }
    
    @Test
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(credentialService.findById(anyInt()))
            .thenThrow(new CredentialNotFoundException("Credential not found"));
        
        mockMvc.perform(get("/api/credentials/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(credentialService, times(1)).findById(99);
    }
    
    @Test
    void save_shouldCreateNewCredential() throws Exception {
        when(credentialService.save(any(CredentialDto.class))).thenReturn(credentialDto);
        
        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
        
        verify(credentialService, times(1)).save(any(CredentialDto.class));
    }
    
    @Test
    void save_shouldReturn400WhenUsernameExists() throws Exception {
        when(credentialService.save(any(CredentialDto.class)))
            .thenThrow(new UsernameAlreadyExistsException("Username already exists"));
        
        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isBadRequest());
        
        verify(credentialService, times(1)).save(any(CredentialDto.class));
    }
    
    @Test
    void update_shouldUpdateCredential() throws Exception {
        when(credentialService.update(any(CredentialDto.class))).thenReturn(credentialDto);
        
        mockMvc.perform(put("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
        
        verify(credentialService, times(1)).update(any(CredentialDto.class));
    }
    
    @Test
    void updateWithId_shouldUpdateCredential() throws Exception {
        when(credentialService.update(anyInt(), any(CredentialDto.class))).thenReturn(credentialDto);
        
        mockMvc.perform(put("/api/credentials/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
        
        verify(credentialService, times(1)).update(eq(1), any(CredentialDto.class));
    }
    
    @Test
    void deleteById_shouldDeleteCredential() throws Exception {
        doNothing().when(credentialService).deleteById(anyInt());
        
        mockMvc.perform(delete("/api/credentials/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(credentialService, times(1)).deleteById(1);
    }
    
    @Test
    void deleteById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new CredentialNotFoundException("Credential not found"))
            .when(credentialService).deleteById(anyInt());
        
        mockMvc.perform(delete("/api/credentials/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(credentialService, times(1)).deleteById(99);
    }
}