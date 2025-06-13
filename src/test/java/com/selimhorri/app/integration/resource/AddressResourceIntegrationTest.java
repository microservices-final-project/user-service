package com.selimhorri.app.integration.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
public class AddressResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        // Configuración básica del AddressDto
        addressDto = new AddressDto();
        addressDto.setAddressId(1);
        addressDto.setFullAddress("123 Main Street");
        addressDto.setPostalCode("12345");
        addressDto.setCity("New York");

        // Configuración del UserDto asociado
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        addressDto.setUserDto(userDto);
    }

    @Test
    void findAll_ShouldReturnAllAddresses() throws Exception {
        List<AddressDto> addresses = Collections.singletonList(addressDto);
        given(addressService.findAll()).willReturn(addresses);

        mockMvc.perform(get("/api/address")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].fullAddress").value("123 Main Street"));
    }

    @Test
    void findById_ShouldReturnAddress() throws Exception {
        given(addressService.findById(anyInt())).willReturn(addressDto);

        mockMvc.perform(get("/api/address/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
    }

    @Test
    void findById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/address/ ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        given(addressService.findById(anyInt()))
                .willThrow(new AddressNotFoundException("Address not found"));

        mockMvc.perform(get("/api/address/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ShouldReturnSavedAddress() throws Exception {
        given(addressService.save(any(AddressDto.class))).willReturn(addressDto);

        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
    }

    @Test
    void save_WithNullInput_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturnUpdatedAddress() throws Exception {
        given(addressService.update(any(AddressDto.class))).willReturn(addressDto);

        mockMvc.perform(put("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullAddress").value("123 Main Street"));
    }

    @Test
    void updateWithId_ShouldReturnUpdatedAddress() throws Exception {
        given(addressService.update(anyInt(), any(AddressDto.class))).willReturn(addressDto);

        mockMvc.perform(put("/api/address/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
    }

    @Test
    void deleteById_ShouldReturnTrue() throws Exception {
        doNothing().when(addressService).deleteById(anyInt());

        mockMvc.perform(delete("/api/address/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        doThrow(new AddressNotFoundException("Address not found"))
                .when(addressService).deleteById(anyInt());

        mockMvc.perform(delete("/api/address/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}