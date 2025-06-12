package com.selimhorri.app.unit.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Configura un UserDto de prueba
        userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPhone("1234567890");

        // Configura un AddressDto de prueba
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressId(1);
        addressDto.setFullAddress("123 Main St");
        addressDto.setPostalCode("12345");
        addressDto.setCity("New York");
        
        userDto.setAddressDtos(Set.of(addressDto));

        // Configura un CredentialDto de prueba
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setUsername("johndoe");
        credentialDto.setPassword("password");
        credentialDto.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        credentialDto.setIsEnabled(true);
        
        userDto.setCredentialDto(credentialDto);
    }

    @Test
    void findAll_ShouldReturnUsers() throws Exception {
        given(userService.findAll()).willReturn(Collections.singletonList(userDto));

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].firstName").value("John"));
    }

    @Test
    void findById_ShouldReturnUser() throws Exception {
        given(userService.findById(anyInt())).willReturn(userDto);

        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void findByUsername_ShouldReturnUser() throws Exception {
        given(userService.findByUsername(anyString())).willReturn(userDto);

        mockMvc.perform(get("/api/users/username/johndoe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void save_ShouldReturnSavedUser() throws Exception {
        given(userService.save(any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void update_ShouldReturnUpdatedUser() throws Exception {
        given(userService.update(any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void updateWithUserId_ShouldReturnUpdatedUser() throws Exception {
        given(userService.update(anyInt(), any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void deleteById_ShouldReturnTrue() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/ ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_WithNullInput_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());
    }
}