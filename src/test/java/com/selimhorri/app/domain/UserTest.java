package com.selimhorri.app.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private User user1;
    private User user2;
    private User user3;
    private Set<Address> addresses;
    private Credential credential;

    @BeforeEach
    void setUp() {
        addresses = new HashSet<>();
        Address address = new Address();
        address.setAddressId(1);
        addresses.add(address);

        credential = new Credential();
        credential.setCredentialId(1);

        user1 = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .addresses(addresses)
                .credential(credential)
                .build();

        user2 = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .addresses(new HashSet<>(addresses)) // Different set instance
                .credential(credential)
                .build();

        user3 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .addresses(new HashSet<>()) // Empty set
                .credential(new Credential()) // Different credential
                .build();
    }

    @Test
    void testEquals() {
        // Test equality with same values (addresses and credential excluded)
        assertEquals(user1, user2);
        
        // Test inequality with different values
        assertNotEquals(user1, user3);
        
        // Test with null
        assertNotEquals(null, user1);
        
        // Test with different class
        assertNotEquals(user1, new Object());
    }

    @Test
    void testHashCode() {
        // Equal objects must have equal hash codes (addresses and credential excluded)
        assertEquals(user1.hashCode(), user2.hashCode());
        
        // Unequal objects should have different hash codes
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testSetAddresses() {
        Set<Address> newAddresses = new HashSet<>();
        Address newAddress = new Address();
        newAddress.setAddressId(2);
        newAddresses.add(newAddress);

        user1.setAddresses(newAddresses);
        
        // Verify the addresses were set correctly
        assertEquals(1, user1.getAddresses().size());

    }

    @Test
    void testCanEqual() {
        // Should be able to equal another User
        assertTrue(user1.canEqual(user2));
        
        // Should not be able to equal non-User objects
        assertFalse(user1.canEqual(new Object()));
        assertFalse(user1.canEqual(null));
    }

    @Test
    void testExcludedFieldsInEqualsAndHashCode() {
        // Create a user with different addresses but same other fields
        Set<Address> differentAddresses = new HashSet<>();
        Address diffAddress = new Address();
        diffAddress.setAddressId(3);
        differentAddresses.add(diffAddress);
        
        User user4 = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .addresses(differentAddresses) // Different addresses
                .credential(new Credential()) // Different credential
                .build();
        
        // Should still be equal because addresses and credential are excluded
        assertEquals(user1, user4);
        assertEquals(user1.hashCode(), user4.hashCode());
    }

}