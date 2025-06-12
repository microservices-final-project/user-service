package com.selimhorri.app.integration.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.exception.wrapper.UsernameAlreadyExistsException;
import com.selimhorri.app.service.CredentialService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
public class CredentialResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CredentialService credentialService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private CredentialDto credentialDto;

    @BeforeEach
    void setUp() {
        // Configuración básica del DTO
        credentialDto = new CredentialDto();
        credentialDto.setCredentialId(1);
        credentialDto.setUsername("testuser");
        credentialDto.setPassword("encodedPassword");
        credentialDto.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        credentialDto.setIsEnabled(true);
        credentialDto.setIsAccountNonExpired(true);
        credentialDto.setIsAccountNonLocked(true);
        credentialDto.setIsCredentialsNonExpired(true);

        // Configuración del UserDto asociado
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        credentialDto.setUserDto(userDto);

        // Configuración del mock para passwordEncoder
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
    }

    @Test
    void findAll_ShouldReturnAllCredentials() throws Exception {
        List<CredentialDto> credentials = Collections.singletonList(credentialDto);
        given(credentialService.findAll()).willReturn(credentials);

        mockMvc.perform(get("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].username").value("testuser"));
    }

    @Test
    void findByUsername_ShouldReturnCredential() throws Exception {
        given(credentialService.findByUsername(anyString())).willReturn(credentialDto);

        mockMvc.perform(get("/api/credentials/username/testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void findById_ShouldReturnCredential() throws Exception {
        given(credentialService.findById(anyInt())).willReturn(credentialDto);

        mockMvc.perform(get("/api/credentials/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
    }

    @Test
    void save_ShouldReturnSavedCredential() throws Exception {
        given(credentialService.save(any(CredentialDto.class))).willReturn(credentialDto);

        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
    }

    @Test
    void save_WithExistingUsername_ShouldReturnConflict() throws Exception {
        given(credentialService.save(any(CredentialDto.class)))
                .willThrow(new UsernameAlreadyExistsException("Username already exists"));

        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_ShouldReturnUpdatedCredential() throws Exception {
        given(credentialService.update(any(CredentialDto.class))).willReturn(credentialDto);

        mockMvc.perform(put("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateWithId_ShouldReturnUpdatedCredential() throws Exception {
        given(credentialService.update(anyInt(), any(CredentialDto.class))).willReturn(credentialDto);

        mockMvc.perform(put("/api/credentials/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1));
    }

    @Test
    void deleteById_ShouldReturnTrue() throws Exception {
        mockMvc.perform(delete("/api/credentials/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        doThrow(new CredentialNotFoundException("Credential not found"))
                .when(credentialService).deleteById(anyInt());

        mockMvc.perform(delete("/api/credentials/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByUsername_WithNonExistingUsername_ShouldReturnNotFound() throws Exception {
        given(credentialService.findByUsername(anyString()))
                .willThrow(new UserObjectNotFoundException("Credential not found"));

        mockMvc.perform(get("/api/credentials/username/nonexisting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/credentials/ ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_WithNullInput_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());
    }
}