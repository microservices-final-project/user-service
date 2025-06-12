package com.selimhorri.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.RoleBasedAuthority;

class CredentialDtoEqualsTest {

    private CredentialDto credentialDto1;
    private CredentialDto credentialDto2;
    private CredentialDto credentialDto3;
    private UserDto userDto;
    private Set<VerificationTokenDto> verificationTokenDtos;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .build();

        verificationTokenDtos = new HashSet<>();
        verificationTokenDtos.add(VerificationTokenDto.builder()
                .verificationTokenId(1)
                .token("token123")
                .build());

        credentialDto1 = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .password("encodedPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .userDto(userDto)
                .verificationTokenDtos(verificationTokenDtos)
                .build();

        credentialDto2 = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .password("encodedPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .userDto(userDto)
                .verificationTokenDtos(new HashSet<>(verificationTokenDtos))
                .build();

        credentialDto3 = CredentialDto.builder()
                .credentialId(2)
                .username("janedoe")
                .password("differentPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
                .isEnabled(false)
                .isAccountNonExpired(false)
                .isAccountNonLocked(false)
                .isCredentialsNonExpired(false)
                .userDto(UserDto.builder().userId(2).build())
                .verificationTokenDtos(new HashSet<>())
                .build();
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(credentialDto1, credentialDto1);
    }

    @Test
    void testEquals_EqualObjects() {
        assertEquals(credentialDto1, credentialDto2);
    }

    @Test
    void testEquals_DifferentObjects() {
        assertNotEquals(credentialDto1, credentialDto3);
    }

    @Test
    void testEquals_NullComparison() {
        assertNotEquals(null, credentialDto1);
    }

    @Test
    void testEquals_DifferentClass() {
        assertNotEquals(credentialDto1, new Object());
    }

    @Test
    void testEquals_DifferentCredentialId() {
        CredentialDto differentId = CredentialDto.builder()
                .credentialId(999)
                .username("johndoe")
                .password("encodedPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .build();
        assertNotEquals(credentialDto1, differentId);
    }

    @Test
    void testEquals_DifferentUsername() {
        CredentialDto differentUsername = CredentialDto.builder()
                .credentialId(1)
                .username("different")
                .password("encodedPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .build();
        assertNotEquals(credentialDto1, differentUsername);
    }

    @Test
    void testEquals_DifferentPassword() {
        CredentialDto differentPassword = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .password("different")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .build();
        assertNotEquals(credentialDto1, differentPassword);
    }

    @Test
    void testEquals_DifferentRole() {
        CredentialDto differentRole = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .password("encodedPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
                .build();
        assertNotEquals(credentialDto1, differentRole);
    }

    @Test
    void testEquals_DifferentStatusFlags() {
        CredentialDto differentStatus = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .password("encodedPassword")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(false)
                .isAccountNonExpired(false)
                .isAccountNonLocked(false)
                .isCredentialsNonExpired(false)
                .build();
        assertNotEquals(credentialDto1, differentStatus);
    }

    @Test
    void testEquals_NullFields() {
        CredentialDto nullFields = CredentialDto.builder()
                .credentialId(null)
                .username(null)
                .password(null)
                .roleBasedAuthority(null)
                .build();
        
        CredentialDto alsoNullFields = CredentialDto.builder()
                .credentialId(null)
                .username(null)
                .password(null)
                .roleBasedAuthority(null)
                .build();
        
        assertEquals(nullFields, alsoNullFields);
        assertNotEquals(credentialDto1, nullFields);
    }
}