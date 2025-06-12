package com.selimhorri.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDtoEqualsTest {

    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private Set<AddressDto> addressDtos;
    private CredentialDto credentialDto;

    @BeforeEach
    void setUp() {
        addressDtos = new HashSet<>();
        addressDtos.add(AddressDto.builder()
                .addressId(1)
                .fullAddress("123 Main St")
                .build());

        credentialDto = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .build();

        userDto1 = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .addressDtos(addressDtos)
                .credentialDto(credentialDto)
                .build();

        userDto2 = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .addressDtos(new HashSet<>(addressDtos)) // Different instance
                .credentialDto(CredentialDto.builder() // Different instance but same values
                        .credentialId(1)
                        .username("johndoe")
                        .build())
                .build();

        userDto3 = UserDto.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .addressDtos(new HashSet<>())
                .credentialDto(CredentialDto.builder()
                        .credentialId(2)
                        .username("janesmith")
                        .build())
                .build();
    }

    @Test
    void testEquals_Reflexivity() {
        assertEquals(userDto1, userDto1);
    }

    @Test
    void testEquals_Symmetry() {
        assertEquals(userDto1, userDto2);
        assertEquals(userDto2, userDto1);
    }

    @Test
    void testEquals_Consistency() {
        // Multiple calls should return same result
        assertEquals(userDto1, userDto2);
        assertEquals(userDto1, userDto2);
        assertNotEquals(userDto1, userDto3);
        assertNotEquals(userDto1, userDto3);
    }

    @Test
    void testEquals_NullComparison() {
        assertNotEquals(null, userDto1);
    }

    @Test
    void testEquals_DifferentClass() {
        assertNotEquals(userDto1, new Object());
    }

    @Test
    void testEquals_DifferentUserId() {
        UserDto differentId = UserDto.builder()
                .userId(999)
                .firstName("John")
                .lastName("Doe")
                .build();
        assertNotEquals(userDto1, differentId);
    }

    @Test
    void testEquals_DifferentFirstName() {
        UserDto differentFirstName = UserDto.builder()
                .userId(1)
                .firstName("Different")
                .lastName("Doe")
                .build();
        assertNotEquals(userDto1, differentFirstName);
    }

    @Test
    void testEquals_DifferentLastName() {
        UserDto differentLastName = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Different")
                .build();
        assertNotEquals(userDto1, differentLastName);
    }

    @Test
    void testEquals_DifferentEmail() {
        UserDto differentEmail = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("different@example.com")
                .build();
        assertNotEquals(userDto1, differentEmail);
    }

    @Test
    void testEquals_DifferentPhone() {
        UserDto differentPhone = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .phone("9999999999")
                .build();
        assertNotEquals(userDto1, differentPhone);
    }

    @Test
    void testEquals_NullFields() {
        UserDto nullFields = UserDto.builder()
                .userId(null)
                .firstName(null)
                .lastName(null)
                .email(null)
                .phone(null)
                .build();
        
        UserDto alsoNullFields = UserDto.builder()
                .userId(null)
                .firstName(null)
                .lastName(null)
                .email(null)
                .phone(null)
                .build();
        
        assertEquals(nullFields, alsoNullFields);
        assertNotEquals(userDto1, nullFields);
    }

    @Test
    void testEquals_OnlyRequiredFields() {
        UserDto minimal1 = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .build();
        
        UserDto minimal2 = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .build();
        
        assertEquals(minimal1, minimal2);
    }
}