package com.selimhorri.app.resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.exception.handler.GlobalExceptionHandler;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.service.VerificationTokenService;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenResourceTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @Mock
    private VerificationTokenService verificationTokenService;
    
    @InjectMocks
    private VerificationTokenResource verificationTokenResource;
    
    private VerificationTokenDto verificationTokenDto;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para manejar LocalDate
        
        mockMvc = MockMvcBuilders.standaloneSetup(verificationTokenResource)
                .setControllerAdvice(new GlobalExceptionHandler()) // Si tienes un manejador global
                .build();
        
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setCredentialId(1);
        credentialDto.setUsername("testuser");
        
        verificationTokenDto = new VerificationTokenDto();
        verificationTokenDto.setVerificationTokenId(1);
        verificationTokenDto.setToken("test-token");
        verificationTokenDto.setExpireDate(LocalDate.now().plusDays(1));
        verificationTokenDto.setCredentialDto(credentialDto);
    }
    
    @Test
    void findAll_shouldReturnAllVerificationTokens() throws Exception {
        when(verificationTokenService.findAll()).thenReturn(List.of(verificationTokenDto));
        
        mockMvc.perform(get("/api/verificationTokens")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].verificationTokenId").value(1))
                .andExpect(jsonPath("$.collection[0].token").value("test-token"));
        
        verify(verificationTokenService, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnTokenWhenFound() throws Exception {
        when(verificationTokenService.findById(anyInt())).thenReturn(verificationTokenDto);
        
        mockMvc.perform(get("/api/verificationTokens/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationTokenId").value(1))
                .andExpect(jsonPath("$.token").value("test-token"));
        
        verify(verificationTokenService, times(1)).findById(1);
    }
    
    @Test
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(verificationTokenService.findById(anyInt()))
            .thenThrow(new VerificationTokenNotFoundException("Token not found"));
        
        mockMvc.perform(get("/api/verificationTokens/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(verificationTokenService, times(1)).findById(99);
    }
    
    @Test
    void save_shouldCreateNewToken() throws Exception {
        when(verificationTokenService.save(any(VerificationTokenDto.class))).thenReturn(verificationTokenDto);
        
        mockMvc.perform(post("/api/verificationTokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationTokenDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationTokenId").value(1));
        
        verify(verificationTokenService, times(1)).save(any(VerificationTokenDto.class));
    }
    
    @Test
    void updateWithId_shouldUpdateToken() throws Exception {
        when(verificationTokenService.update(anyInt(), any(VerificationTokenDto.class))).thenReturn(verificationTokenDto);
        
        mockMvc.perform(put("/api/verificationTokens/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationTokenDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationTokenId").value(1));
        
        verify(verificationTokenService, times(1)).update(eq(1), any(VerificationTokenDto.class));
    }
    
    @Test
    void deleteById_shouldDeleteToken() throws Exception {
        doNothing().when(verificationTokenService).deleteById(anyInt());
        
        mockMvc.perform(delete("/api/verificationTokens/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(verificationTokenService, times(1)).deleteById(1);
    }
    
    @Test
    void deleteById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new VerificationTokenNotFoundException("Token not found"))
            .when(verificationTokenService).deleteById(anyInt());
        
        mockMvc.perform(delete("/api/verificationTokens/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(verificationTokenService, times(1)).deleteById(99);
    }
}