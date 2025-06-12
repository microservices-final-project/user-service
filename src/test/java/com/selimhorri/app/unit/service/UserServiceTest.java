package com.selimhorri.app.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private CredentialRepository credentialRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User userWithCredential;
    private User userWithoutCredential;
    private Credential credential;
    
    @BeforeEach
    void setUp() {
        credential = new Credential();
        credential.setCredentialId(1);
        credential.setUsername("testuser");
        credential.setPassword("password");
        credential.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER);
        credential.setIsEnabled(true);
        credential.setIsAccountNonExpired(true);
        credential.setIsAccountNonLocked(true);
        credential.setIsCredentialsNonExpired(true);
        
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setVerificationTokenId(1);
        verificationToken.setToken("token123");
        verificationToken.setExpireDate(LocalDate.now().plusDays(1));
        verificationToken.setCredential(credential);
        
        credential.setVerificationTokens(Collections.singleton(verificationToken));
        
        userWithCredential = new User();
        userWithCredential.setUserId(1);
        userWithCredential.setFirstName("John");
        userWithCredential.setLastName("Doe");
        userWithCredential.setEmail("john.doe@example.com");
        userWithCredential.setPhone("1234567890");
        userWithCredential.setCredential(credential);
        
        userWithoutCredential = new User();
        userWithoutCredential.setUserId(2);
        userWithoutCredential.setFirstName("Jane");
        userWithoutCredential.setLastName("Doe");
        userWithoutCredential.setEmail("jane.doe@example.com");
        userWithoutCredential.setPhone("0987654321");
        userWithoutCredential.setCredential(null);
    }
    
    // Builders para DTOs (como clases internas estáticas)
    static class UserDtoBuilder {
        private Integer userId;
        private String firstName;
        private String lastName;
        private String imageUrl;
        private String email;
        private String phone;
        private CredentialDto credentialDto;
        
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
        
        UserDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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
        
        UserDtoBuilder withCredentialDto(CredentialDto credentialDto) {
            this.credentialDto = credentialDto;
            return this;
        }
        
        UserDto build() {
            UserDto userDto = new UserDto();
            userDto.setUserId(this.userId);
            userDto.setFirstName(this.firstName);
            userDto.setLastName(this.lastName);
            userDto.setImageUrl(this.imageUrl);
            userDto.setEmail(this.email);
            userDto.setPhone(this.phone);
            userDto.setCredentialDto(this.credentialDto);
            return userDto;
        }
    }
    
    static class CredentialDtoBuilder {
        private Integer credentialId;
        private String username;
        private String password;
        private RoleBasedAuthority roleBasedAuthority;
        private Boolean isEnabled;
        private Boolean isAccountNonExpired;
        private Boolean isAccountNonLocked;
        private Boolean isCredentialsNonExpired;
        
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
        
        CredentialDto build() {
            CredentialDto credentialDto = new CredentialDto();
            credentialDto.setCredentialId(this.credentialId);
            credentialDto.setUsername(this.username);
            credentialDto.setPassword(this.password);
            credentialDto.setRoleBasedAuthority(this.roleBasedAuthority);
            credentialDto.setIsEnabled(this.isEnabled);
            credentialDto.setIsAccountNonExpired(this.isAccountNonExpired);
            credentialDto.setIsAccountNonLocked(this.isAccountNonLocked);
            credentialDto.setIsCredentialsNonExpired(this.isCredentialsNonExpired);
            return credentialDto;
        }
    }
    
    @Test
    void findAll_shouldReturnListOfUsersWithCredentials() {
        when(userRepository.findAll()).thenReturn(List.of(userWithCredential, userWithoutCredential));
        
        List<UserDto> result = userService.findAll();
        
        assertEquals(1, result.size());
        assertEquals(userWithCredential.getUserId(), result.get(0).getUserId());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void findAll_shouldReturnEmptyListWhenNoUsersWithCredentials() {
        when(userRepository.findAll()).thenReturn(List.of(userWithoutCredential));
        
        List<UserDto> result = userService.findAll();
        
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnUserWithCredentials() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userWithCredential));
        
        UserDto result = userService.findById(1);
        
        assertNotNull(result);
        assertEquals(userWithCredential.getUserId(), result.getUserId());
        assertNotNull(result.getCredentialDto());
        verify(userRepository, times(1)).findById(1);
    }
    
    @Test
    void findById_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(UserObjectNotFoundException.class, () -> userService.findById(99));
        verify(userRepository, times(1)).findById(99);
    }
    
    @Test
    void findById_shouldThrowExceptionWhenUserHasNoCredentials() {
        when(userRepository.findById(2)).thenReturn(Optional.of(userWithoutCredential));
        
        assertThrows(UserObjectNotFoundException.class, () -> userService.findById(2));
        verify(userRepository, times(1)).findById(2);
    }
    
    @Test
    void findByUsername_shouldReturnUserWithGivenUsername() {
        when(userRepository.findByCredentialUsername("testuser")).thenReturn(Optional.of(userWithCredential));
        
        UserDto result = userService.findByUsername("testuser");
        
        assertNotNull(result);
        assertEquals(userWithCredential.getUserId(), result.getUserId());
        assertEquals("testuser", result.getCredentialDto().getUsername());
        verify(userRepository, times(1)).findByCredentialUsername("testuser");
    }
    
    @Test
    void findByUsername_shouldThrowExceptionWhenUsernameNotFound() {
        when(userRepository.findByCredentialUsername("unknown")).thenReturn(Optional.empty());
        
        assertThrows(UserObjectNotFoundException.class, () -> userService.findByUsername("unknown"));
        verify(userRepository, times(1)).findByCredentialUsername("unknown");
    }
    
    @Test
    void save_shouldReturnSavedUser() {
        UserDto userDto = new UserDtoBuilder()
                .withFirstName("New")
                .withLastName("User")
                .withEmail("new.user@example.com")
                .withPhone("5555555555")
                .build();
        
        User userToSave = UserMappingHelper.mapOnlyUser(userDto);
        User savedUser = new User();
        savedUser.setUserId(3);
        savedUser.setFirstName(userToSave.getFirstName());
        savedUser.setLastName(userToSave.getLastName());
        savedUser.setEmail(userToSave.getEmail());
        savedUser.setPhone(userToSave.getPhone());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        UserDto result = userService.save(userDto);
        
        assertNotNull(result);
        assertEquals(3, result.getUserId());
        assertEquals("New", result.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void update_shouldUpdateExistingUser() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(1)
                .withFirstName("Updated")
                .withLastName("Name")
                .withEmail("updated.email@example.com")
                .withPhone("9999999999")
                .build();
        
        when(userRepository.findById(1)).thenReturn(Optional.of(userWithCredential));
        when(userRepository.save(any(User.class))).thenReturn(userWithCredential);
        
        UserDto result = userService.update(userDto);
        
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void update_shouldThrowExceptionWhenUserNotFound() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(99)
                .withFirstName("Non")
                .withLastName("Existent")
                .build();
        
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> userService.update(userDto));
        verify(userRepository, times(1)).findById(99);
    }
    
    @Test
    void update_shouldThrowExceptionWhenUserHasNoCredentials() {
        UserDto userDto = new UserDtoBuilder()
                .withUserId(2)
                .withFirstName("No")
                .withLastName("Credential")
                .build();
        
        when(userRepository.findById(2)).thenReturn(Optional.of(userWithoutCredential));
        
        assertThrows(EntityNotFoundException.class, () -> userService.update(userDto));
        verify(userRepository, times(1)).findById(2);
    }
    
    @Test
    void updateWithUserId_shouldUpdateExistingUser() {
        UserDto userDto = new UserDtoBuilder()
                .withFirstName("Updated")
                .withLastName("Name")
                .withEmail("updated.email@example.com")
                .withPhone("9999999999")
                .build();
        
        when(userRepository.findById(1)).thenReturn(Optional.of(userWithCredential));
        when(userRepository.save(any(User.class))).thenReturn(userWithCredential);
        
        UserDto result = userService.update(1, userDto);
        
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void deleteById_shouldDeleteUserCredentials() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userWithCredential));
        doNothing().when(credentialRepository).deleteByCredentialId(1);
        
        userService.deleteById(1);
        
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
        verify(credentialRepository, times(1)).deleteByCredentialId(1);
    }
    
    @Test
    void deleteById_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(99));
        verify(userRepository, times(1)).findById(99);
    }
    
    @Test
    void deleteById_shouldThrowExceptionWhenUserHasNoCredentials() {
        when(userRepository.findById(2)).thenReturn(Optional.of(userWithoutCredential));
        
        assertThrows(UserObjectNotFoundException.class, () -> userService.deleteById(2));
        verify(userRepository, times(1)).findById(2);
    }
}