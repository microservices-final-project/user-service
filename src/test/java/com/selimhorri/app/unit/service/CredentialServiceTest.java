package com.selimhorri.app.unit.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.exception.wrapper.UsernameAlreadyExistsException;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.CredentialServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CredentialServiceTest {

    @Mock
    private CredentialRepository credentialRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private CredentialServiceImpl credentialService;
    
    private Credential credential;
    private User user;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("1234567890");
        
        credential = new Credential();
        credential.setCredentialId(1);
        credential.setUsername("johndoe");
        credential.setPassword("encodedPassword");
        credential.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        credential.setIsEnabled(true);
        credential.setIsAccountNonExpired(true);
        credential.setIsAccountNonLocked(true);
        credential.setIsCredentialsNonExpired(true);
        credential.setUser(user);
    }
    
    // Builders para DTOs
    static class CredentialDtoBuilder {
        private Integer credentialId;
        private String username;
        private String password;
        private RoleBasedAuthority roleBasedAuthority;
        private Boolean isEnabled;
        private Boolean isAccountNonExpired;
        private Boolean isAccountNonLocked;
        private Boolean isCredentialsNonExpired;
        private UserDto userDto;
        
        CredentialDtoBuilder withCredentialId(Integer credentialId) {
            this.credentialId = credentialId;
            return this;
        }
        
        CredentialDtoBuilder withUsername(String username) {
            this.username = username;
            return this;
        }
        
        CredentialDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }
        
        CredentialDtoBuilder withRoleBasedAuthority(RoleBasedAuthority roleBasedAuthority) {
            this.roleBasedAuthority = roleBasedAuthority;
            return this;
        }
        
        CredentialDtoBuilder withIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }
        
        CredentialDtoBuilder withIsAccountNonExpired(Boolean isAccountNonExpired) {
            this.isAccountNonExpired = isAccountNonExpired;
            return this;
        }
        
        CredentialDtoBuilder withIsAccountNonLocked(Boolean isAccountNonLocked) {
            this.isAccountNonLocked = isAccountNonLocked;
            return this;
        }
        
        CredentialDtoBuilder withIsCredentialsNonExpired(Boolean isCredentialsNonExpired) {
            this.isCredentialsNonExpired = isCredentialsNonExpired;
            return this;
        }
        
        CredentialDtoBuilder withUserDto(UserDto userDto) {
            this.userDto = userDto;
            return this;
        }
        
        CredentialDto build() {
            CredentialDto dto = new CredentialDto();
            dto.setCredentialId(this.credentialId);
            dto.setUsername(this.username);
            dto.setPassword(this.password);
            dto.setRoleBasedAuthority(this.roleBasedAuthority);
            dto.setIsEnabled(this.isEnabled);
            dto.setIsAccountNonExpired(this.isAccountNonExpired);
            dto.setIsAccountNonLocked(this.isAccountNonLocked);
            dto.setIsCredentialsNonExpired(this.isCredentialsNonExpired);
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
    void findAll_shouldReturnListOfCredentials() {
        when(credentialRepository.findAll()).thenReturn(List.of(credential));
        
        List<CredentialDto> result = credentialService.findAll();
        
        assertEquals(1, result.size());
        assertEquals(credential.getCredentialId(), result.get(0).getCredentialId());
        verify(credentialRepository, times(1)).findAll();
    }
    
    @Test
    void findAll_shouldReturnEmptyListWhenNoCredentials() {
        when(credentialRepository.findAll()).thenReturn(List.of());
        
        List<CredentialDto> result = credentialService.findAll();
        
        assertTrue(result.isEmpty());
        verify(credentialRepository, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnCredentialWhenFound() {
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
        
        CredentialDto result = credentialService.findById(1);
        
        assertNotNull(result);
        assertEquals(credential.getCredentialId(), result.getCredentialId());
        verify(credentialRepository, times(1)).findById(1);
    }
    
    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(credentialRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(CredentialNotFoundException.class, () -> credentialService.findById(99));
        verify(credentialRepository, times(1)).findById(99);
    }
    
    @Test
    void findByUsername_shouldReturnCredentialWhenFound() {
        when(credentialRepository.findByUsername("johndoe")).thenReturn(Optional.of(credential));
        
        CredentialDto result = credentialService.findByUsername("johndoe");
        
        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(credentialRepository, times(1)).findByUsername("johndoe");
    }
    
    @Test
    void findByUsername_shouldThrowExceptionWhenNotFound() {
        when(credentialRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        
        assertThrows(UserObjectNotFoundException.class, () -> credentialService.findByUsername("unknown"));
        verify(credentialRepository, times(1)).findByUsername("unknown");
    }
    
    @Test
    void save_shouldSaveNewCredential() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(1)
                .withFirstName("John")
                .withLastName("Doe")
                .build();
        
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withUsername("newuser")
                .withPassword("rawPassword")
                .withRoleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .withUserDto(userDto)
                .build();
        
        when(credentialRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(credentialRepository.existsByUserUserId(1)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
        
        CredentialDto result = credentialService.save(credentialDto);
        
        assertNotNull(result);
        assertEquals(1, result.getCredentialId());
        verify(credentialRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).findById(1);
        verify(credentialRepository, times(1)).existsByUserUserId(1);
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }
    
    @Test
    void save_shouldThrowExceptionWhenUsernameExists() {
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withUsername("existinguser")
                .build();
        
        when(credentialRepository.existsByUsername("existinguser")).thenReturn(true);
        
        assertThrows(UsernameAlreadyExistsException.class, () -> credentialService.save(credentialDto));
        verify(credentialRepository, times(1)).existsByUsername("existinguser");
    }
    
    @Test
    void save_shouldThrowExceptionWhenUserNotFound() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(99)
                .build();
        
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withUsername("newuser")
                .withUserDto(userDto)
                .build();
        
        when(credentialRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(UserObjectNotFoundException.class, () -> credentialService.save(credentialDto));
        verify(credentialRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).findById(99);
    }
    
    @Test
    void save_shouldThrowExceptionWhenUserAlreadyHasCredentials() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(1)
                .build();
        
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withUsername("newuser")
                .withUserDto(userDto)
                .build();
        
        when(credentialRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(credentialRepository.existsByUserUserId(1)).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> credentialService.save(credentialDto));
        verify(credentialRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).findById(1);
        verify(credentialRepository, times(1)).existsByUserUserId(1);
    }
    
    @Test
    void update_shouldUpdateExistingCredential() {
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withCredentialId(1)
                .withUsername("updateduser")
                .withPassword("newPassword")
                .withRoleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
                .withIsEnabled(false)
                .build();
        
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
        
        CredentialDto result = credentialService.update(credentialDto);
        
        assertNotNull(result);
        assertEquals(1, result.getCredentialId());
        verify(credentialRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }
    
    @Test
    void update_shouldThrowExceptionWhenCredentialNotFound() {
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withCredentialId(99)
                .build();
        
        when(credentialRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(CredentialNotFoundException.class, () -> credentialService.update(credentialDto));
        verify(credentialRepository, times(1)).findById(99);
    }
    
    @Test
    void updateWithId_shouldUpdateExistingCredential() {
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withUsername("updateduser")
                .withPassword("newPassword")
                .withRoleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
                .withIsEnabled(false)
                .build();
        
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
        
        CredentialDto result = credentialService.update(1, credentialDto);
        
        assertNotNull(result);
        assertEquals(1, result.getCredentialId());
        verify(credentialRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }
    
    @Test
    void deleteById_shouldDeleteCredentialWhenFound() {
        when(credentialRepository.existsById(1)).thenReturn(true);
        doNothing().when(credentialRepository).deleteByCredentialId(1);
        
        credentialService.deleteById(1);
        
        verify(credentialRepository, times(1)).existsById(1);
        verify(credentialRepository, times(1)).deleteByCredentialId(1);
    }
    
    @Test
    void deleteById_shouldThrowExceptionWhenNotFound() {
        when(credentialRepository.existsById(99)).thenReturn(false);
        
        assertThrows(CredentialNotFoundException.class, () -> credentialService.deleteById(99));
        verify(credentialRepository, times(1)).existsById(99);
    }
}