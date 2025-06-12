package com.selimhorri.app.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CredentialTest {

    private Credential credential1;
    private Credential credential2;
    private Credential credential3;
    private User user;
    private Set<VerificationToken> verificationTokens;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        
        verificationTokens = new HashSet<>();
        VerificationToken token = new VerificationToken();
        token.setVerificationTokenId(1);
        verificationTokens.add(token);

        credential1 = Credential.builder()
                .credentialId(1)
                .username("user1")
                .password("password1")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(user)
                .verificationTokens(verificationTokens)
                .build();

        credential2 = Credential.builder()
                .credentialId(1)
                .username("user1")
                .password("password1")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(user)
                .verificationTokens(verificationTokens)
                .build();

        credential3 = Credential.builder()
                .credentialId(2)
                .username("user2")
                .password("password2")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
                .isEnabled(false)
                .isAccountNonExpired(false)
                .isAccountNonLocked(false)
                .isCredentialsNonExpired(false)
                .user(new User()) // Different user
                .verificationTokens(new HashSet<>()) // Empty set
                .build();
    }

    @Test
    void testEquals() {
        // Test equality with same values
        assertEquals(credential1, credential2);
        
        // Test inequality with different values
        assertNotEquals(credential1, credential3);
        
        // Test with null
        assertNotEquals(null, credential1);
        
        // Test with different class
        assertNotEquals(credential1, new Object());
    }

    @Test
    void testHashCode() {
        // Equal objects must have equal hash codes
        assertEquals(credential1.hashCode(), credential2.hashCode());
        
        // Unequal objects should ideally have different hash codes
        assertNotEquals(credential1.hashCode(), credential3.hashCode());
    }

    @Test
    void testToString() {
        String toString = credential1.toString();
        
        // Verify some key fields are included
        assertTrue(toString.contains("credentialId=1"));
        assertTrue(toString.contains("username=user1"));
        assertTrue(toString.contains("roleBasedAuthority=ROLE_USER"));
        
    }

    @Test
    void testCanEqual() {
        // Should be able to equal another Credential
        assertTrue(credential1.canEqual(credential2));
        
        // Should not be able to equal non-Credential objects
        assertFalse(credential1.canEqual(new Object()));
        assertFalse(credential1.canEqual(null));
    }

    @Test
    void testGetVerificationTokens() {
        // Test getter returns the same set we provided
        assertEquals(verificationTokens, credential1.getVerificationTokens());
        
        // Verify the set contains our token
        assertEquals(1, credential1.getVerificationTokens().size());
        assertEquals(1, credential1.getVerificationTokens().iterator().next().getVerificationTokenId());
        
        // Test with empty set
        assertEquals(0, credential3.getVerificationTokens().size());
    }

    @Test
    void testExcludedFields() {
        // Verify @EqualsAndHashCode.Exclude works for user field
        Credential credential4 = Credential.builder()
                .credentialId(1)
                .username("user1")
                .password("password1")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(new User()) // Different user but should not affect equals
                .verificationTokens(verificationTokens)
                .build();
        
        assertEquals(credential1, credential4);
    }
}