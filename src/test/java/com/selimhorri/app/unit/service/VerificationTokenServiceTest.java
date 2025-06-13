package com.selimhorri.app.unit.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.VerificationTokenRepository;
import com.selimhorri.app.service.impl.VerificationTokenServiceImpl;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    
    @Mock
    private CredentialRepository credentialRepository;
    
    @InjectMocks
    private VerificationTokenServiceImpl verificationTokenService;
    
    private VerificationToken verificationToken;
    private Credential credential;
    
    @BeforeEach
    void setUp() {
        credential = new Credential();
        credential.setCredentialId(1);
        credential.setUsername("testuser");
        credential.setPassword("password");
        
        verificationToken = new VerificationToken();
        verificationToken.setVerificationTokenId(1);
        verificationToken.setToken("test-token");
        verificationToken.setExpireDate(LocalDate.now().plusDays(1));
        verificationToken.setCredential(credential);
    }
    
    // Builders para DTOs
    static class VerificationTokenDtoBuilder {
        private Integer verificationTokenId;
        private String token;
        private LocalDate expireDate;
        private CredentialDto credentialDto;
        
        VerificationTokenDtoBuilder withVerificationTokenId(Integer verificationTokenId) {
            this.verificationTokenId = verificationTokenId;
            return this;
        }
        
        VerificationTokenDtoBuilder withToken(String token) {
            this.token = token;
            return this;
        }
        
        VerificationTokenDtoBuilder withExpireDate(LocalDate expireDate) {
            this.expireDate = expireDate;
            return this;
        }
        
        VerificationTokenDtoBuilder withCredentialDto(CredentialDto credentialDto) {
            this.credentialDto = credentialDto;
            return this;
        }
        
        VerificationTokenDto build() {
            VerificationTokenDto dto = new VerificationTokenDto();
            dto.setVerificationTokenId(this.verificationTokenId);
            dto.setToken(this.token);
            dto.setExpireDate(this.expireDate);
            dto.setCredentialDto(this.credentialDto);
            return dto;
        }
    }
    
    static class CredentialDtoBuilder {
        private Integer credentialId;
        private String username;
        
        CredentialDtoBuilder withCredentialId(Integer credentialId) {
            this.credentialId = credentialId;
            return this;
        }
        
        CredentialDtoBuilder withUsername(String username) {
            this.username = username;
            return this;
        }
        
        CredentialDto build() {
            CredentialDto dto = new CredentialDto();
            dto.setCredentialId(this.credentialId);
            dto.setUsername(this.username);
            return dto;
        }
    }
    
    @Test
    void findAll_shouldReturnListOfVerificationTokens() {
        when(verificationTokenRepository.findAll()).thenReturn(List.of(verificationToken));
        
        List<VerificationTokenDto> result = verificationTokenService.findAll();
        
        assertEquals(1, result.size());
        assertEquals(verificationToken.getVerificationTokenId(), result.get(0).getVerificationTokenId());
        verify(verificationTokenRepository, times(1)).findAll();
    }
    
    @Test
    void findAll_shouldReturnEmptyListWhenNoTokens() {
        when(verificationTokenRepository.findAll()).thenReturn(List.of());
        
        List<VerificationTokenDto> result = verificationTokenService.findAll();
        
        assertTrue(result.isEmpty());
        verify(verificationTokenRepository, times(1)).findAll();
    }
    
    @Test
    void findById_shouldReturnTokenWhenFound() {
        when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(verificationToken));
        
        VerificationTokenDto result = verificationTokenService.findById(1);
        
        assertNotNull(result);
        assertEquals(verificationToken.getVerificationTokenId(), result.getVerificationTokenId());
        assertEquals("test-token", result.getToken());
        verify(verificationTokenRepository, times(1)).findById(1);
    }
    
    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(verificationTokenRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(VerificationTokenNotFoundException.class, () -> verificationTokenService.findById(99));
        verify(verificationTokenRepository, times(1)).findById(99);
    }
    
    @Test
    void save_shouldSaveNewTokenWithValidCredential() {
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withCredentialId(1)
                .build();
        
        VerificationTokenDto tokenDto = new VerificationTokenDtoBuilder()
                .withToken("new-token")
                .withExpireDate(LocalDate.now().plusDays(2))
                .withCredentialDto(credentialDto)
                .build();
        
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
        
        VerificationTokenDto result = verificationTokenService.save(tokenDto);
        
        assertNotNull(result);
        assertEquals(1, result.getVerificationTokenId());
        verify(credentialRepository, times(1)).findById(1);
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }
    
    @Test
    void save_shouldThrowExceptionWhenCredentialIdIsNull() {
        VerificationTokenDto tokenDto = new VerificationTokenDtoBuilder()
                .withToken("new-token")
                .withCredentialDto(null) // Sin credencial
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> verificationTokenService.save(tokenDto));
    }
    
    @Test
    void save_shouldThrowExceptionWhenCredentialNotFound() {
        CredentialDto credentialDto = new CredentialDtoBuilder()
                .withCredentialId(99)
                .build();
        
        VerificationTokenDto tokenDto = new VerificationTokenDtoBuilder()
                .withToken("new-token")
                .withCredentialDto(credentialDto)
                .build();
        
        when(credentialRepository.findById(99)).thenReturn(Optional.empty());
        
        assertThrows(CredentialNotFoundException.class, () -> verificationTokenService.save(tokenDto));
        verify(credentialRepository, times(1)).findById(99);
    }
    
    @Test
    void update_shouldUpdateExistingToken() {
        VerificationTokenDto tokenDto = new VerificationTokenDtoBuilder()
                .withVerificationTokenId(1)
                .withToken("updated-token")
                .withExpireDate(LocalDate.now().plusDays(3))
                .build();
        
        when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(verificationToken));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
        
        VerificationTokenDto result = verificationTokenService.update(tokenDto);
        
        assertNotNull(result);
        assertEquals(1, result.getVerificationTokenId());
        verify(verificationTokenRepository, times(1)).findById(1);
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }
    
    @Test
    void update_shouldNotChangeCredentialAssociation() {
        CredentialDto newCredentialDto = new CredentialDtoBuilder()
                .withCredentialId(2) // Intentar cambiar credencial
                .build();
        
        VerificationTokenDto tokenDto = new VerificationTokenDtoBuilder()
                .withVerificationTokenId(1)
                .withToken("updated-token")
                .withCredentialDto(newCredentialDto) // Credencial diferente
                .build();
        
        when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(verificationToken));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
        
        verificationTokenService.update(tokenDto);
        
        // Verificar que la credencial original se mantuvo
        assertEquals(1, verificationToken.getCredential().getCredentialId());
        verify(verificationTokenRepository, times(1)).findById(1);
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }
    
    @Test
    void updateWithId_shouldUpdateExistingToken() {
        VerificationTokenDto tokenDto = new VerificationTokenDtoBuilder()
                .withToken("updated-token")
                .withExpireDate(LocalDate.now().plusDays(3))
                .build();
        
        when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(verificationToken));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
        
        VerificationTokenDto result = verificationTokenService.update(1, tokenDto);
        
        assertNotNull(result);
        assertEquals(1, result.getVerificationTokenId());
        verify(verificationTokenRepository, times(1)).findById(1);
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }
    
    @Test
    void deleteById_shouldDeleteTokenWhenExists() {
        when(verificationTokenRepository.existsById(1)).thenReturn(true);
        doNothing().when(verificationTokenRepository).deleteByIdCustom(1);
        
        verificationTokenService.deleteById(1);
        
        verify(verificationTokenRepository, times(1)).existsById(1);
        verify(verificationTokenRepository, times(1)).deleteByIdCustom(1);
    }
    
    @Test
    void deleteById_shouldThrowExceptionWhenTokenNotFound() {
        when(verificationTokenRepository.existsById(99)).thenReturn(false);
        
        assertThrows(VerificationTokenNotFoundException.class, () -> verificationTokenService.deleteById(99));
        verify(verificationTokenRepository, times(1)).existsById(99);
    }
}