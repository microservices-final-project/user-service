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
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.service.AddressService;

@ExtendWith(MockitoExtension.class)
public class AddressResourceTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @Mock
    private AddressService addressService;
    
    @InjectMocks
    private AddressResource addressResource;
    
    private AddressDto addressDto;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressResource).build();
        objectMapper = new ObjectMapper();
        
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        
        addressDto = new AddressDto();
        addressDto.setAddressId(1);
        addressDto.setFullAddress("123 Main St, Apt 4B");
        addressDto.setPostalCode("12345");
        addressDto.setCity("New York");
        addressDto.setUserDto(userDto);
    }
    
    @Test
    void findAll_shouldReturnAllAddresses() throws Exception {
        when(addressService.findAll()).thenReturn(List.of(addressDto));
        
        mockMvc.perform(get("/api/address")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].addressId").value(1))
                .andExpect(jsonPath("$.collection[0].fullAddress").value("123 Main St, Apt 4B"));
        
        verify(addressService, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnAddressWhenFound() throws Exception {
        when(addressService.findById(anyInt())).thenReturn(addressDto);
        
        mockMvc.perform(get("/api/address/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1))
                .andExpect(jsonPath("$.city").value("New York"));
        
        verify(addressService, times(1)).findById(1);
    }
    

    @Test
    void save_shouldCreateNewAddress() throws Exception {
        when(addressService.save(any(AddressDto.class))).thenReturn(addressDto);
        
        mockMvc.perform(post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
        
        verify(addressService, times(1)).save(any(AddressDto.class));
    }
    
    @Test
    void update_shouldUpdateAddress() throws Exception {
        when(addressService.update(any(AddressDto.class))).thenReturn(addressDto);
        
        mockMvc.perform(put("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
        
        verify(addressService, times(1)).update(any(AddressDto.class));
    }
    
    @Test
    void updateWithId_shouldUpdateAddress() throws Exception {
        when(addressService.update(anyInt(), any(AddressDto.class))).thenReturn(addressDto);
        
        mockMvc.perform(put("/api/address/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
        
        verify(addressService, times(1)).update(eq(1), any(AddressDto.class));
    }
    
    @Test
    void deleteById_shouldDeleteAddress() throws Exception {
        doNothing().when(addressService).deleteById(anyInt());
        
        mockMvc.perform(delete("/api/address/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(addressService, times(1)).deleteById(1);
    }
    
}