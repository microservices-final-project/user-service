package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.repository.AddressRepository;
import com.selimhorri.app.service.impl.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    
    @InjectMocks
    private AddressServiceImpl addressService;
    
    private Address address;
    private User user;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("1234567890");
        
        address = new Address();
        address.setAddressId(1);
        address.setFullAddress("123 Main St, Apt 4B");
        address.setPostalCode("12345");
        address.setCity("New York");
        address.setUser(user);
    }
    
    // Builders para DTOs
    static class AddressDtoBuilder {
        private Integer addressId;
        private String fullAddress;
        private String postalCode;
        private String city;
        private UserDto userDto;
        
        AddressDtoBuilder withAddressId(Integer addressId) {
            this.addressId = addressId;
            return this;
        }
        
        AddressDtoBuilder withFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
            return this;
        }
        
        AddressDtoBuilder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }
        
        AddressDtoBuilder withCity(String city) {
            this.city = city;
            return this;
        }
        
        AddressDtoBuilder withUserDto(UserDto userDto) {
            this.userDto = userDto;
            return this;
        }
        
        AddressDto build() {
            AddressDto dto = new AddressDto();
            dto.setAddressId(this.addressId);
            dto.setFullAddress(this.fullAddress);
            dto.setPostalCode(this.postalCode);
            dto.setCity(this.city);
            dto.setUserDto(this.userDto);
            return dto;
        }
    }
    
    static class UserDtoBuilder {
        private Integer userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        
        UserDtoBuilder withUserId(Integer userId) {
            this.userId = userId;
            return this;
        }
        
        UserDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        UserDtoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        UserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }
        
        UserDtoBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }
        
        UserDto build() {
            UserDto dto = new UserDto();
            dto.setUserId(this.userId);
            dto.setFirstName(this.firstName);
            dto.setLastName(this.lastName);
            dto.setEmail(this.email);
            dto.setPhone(this.phone);
            return dto;
        }
    }
    
    @Test
    void findAll_shouldReturnListOfAddresses() {
        when(addressRepository.findAll()).thenReturn(List.of(address));
        
        List<AddressDto> result = addressService.findAll();
        
        assertEquals(1, result.size());
        assertEquals(address.getAddressId(), result.get(0).getAddressId());
        verify(addressRepository, times(1)).findAll();
    }
    
    @Test
    void findAll_shouldReturnEmptyListWhenNoAddresses() {
        when(addressRepository.findAll()).thenReturn(List.of());
        
        List<AddressDto> result = addressService.findAll();
        
        assertTrue(result.isEmpty());
        verify(addressRepository, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnAddressWhenFound() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        
        AddressDto result = addressService.findById(1);
        
        assertNotNull(result);
        assertEquals(address.getAddressId(), result.getAddressId());
        assertEquals("123 Main St, Apt 4B", result.getFullAddress());
        verify(addressRepository, times(1)).findById(1);
    }
    
    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(AddressNotFoundException.class, () -> addressService.findById(99));
        verify(addressRepository, times(1)).findById(99);
    }
    
    @Test
    void save_shouldSaveNewAddress() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(1)
                .build();
        
        AddressDto addressDto = new AddressDtoBuilder()
                .withFullAddress("456 Oak Ave")
                .withPostalCode("67890")
                .withCity("Chicago")
                .withUserDto(userDto)
                .build();
        
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        
        AddressDto result = addressService.save(addressDto);
        
        assertNotNull(result);
        assertEquals(1, result.getAddressId());
        verify(addressRepository, times(1)).save(any(Address.class));
    }
    
    @Test
    void update_shouldUpdateExistingAddress() {
        AddressDto addressDto = new AddressDtoBuilder()
                .withAddressId(1)
                .withFullAddress("Updated Address")
                .withPostalCode("54321")
                .withCity("Los Angeles")
                .build();
        
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        
        AddressDto result = addressService.update(addressDto);
        
        assertNotNull(result);
        assertEquals(1, result.getAddressId());
        verify(addressRepository, times(1)).findById(1);
        verify(addressRepository, times(1)).save(any(Address.class));
    }
    
    @Test
    void update_shouldThrowExceptionWhenAddressNotFound() {
        AddressDto addressDto = new AddressDtoBuilder()
                .withAddressId(99)
                .build();
        
        when(addressRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(AddressNotFoundException.class, () -> addressService.update(addressDto));
        verify(addressRepository, times(1)).findById(99);
    }
    
    @Test
    void updateWithId_shouldUpdateExistingAddress() {
        AddressDto addressDto = new AddressDtoBuilder()
                .withFullAddress("Updated Address")
                .withPostalCode("54321")
                .withCity("Los Angeles")
                .build();
        
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        
        AddressDto result = addressService.update(1, addressDto);
        
        assertNotNull(result);
        assertEquals(1, result.getAddressId());
        verify(addressRepository, times(1)).findById(1);
        verify(addressRepository, times(1)).save(any(Address.class));
    }
    
    @Test
    void updateWithId_shouldNotChangeUserAssociation() {
        AddressDto addressDto = new AddressDtoBuilder()
                .withFullAddress("Updated Address")
                .withUserDto(new UserDtoBuilder().withUserId(2).build()) // Intentar cambiar usuario
                .build();
        
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        
        AddressDto result = addressService.update(1, addressDto);
        
        // Verificar que el usuario original se mantuvo
        assertEquals(1, address.getUser().getUserId());
        verify(addressRepository, times(1)).findById(1);
        verify(addressRepository, times(1)).save(any(Address.class));
    }
    
    @Test
    void deleteById_shouldDeleteAddress() {
        doNothing().when(addressRepository).deleteById(1);
        
        addressService.deleteById(1);
        
        verify(addressRepository, times(1)).deleteById(1);
    }
}