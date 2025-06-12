package com.selimhorri.app.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressEqualsTest {

    private Address address1;
    private Address address2;
    private Address address3;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .build();

        user2 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        address1 = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("New York")
                .user(user1)
                .build();

        address2 = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("New York")
                .user(user2) // Different user but excluded from equals
                .build();

        address3 = Address.builder()
                .addressId(2)
                .fullAddress("456 Oak Ave")
                .postalCode("67890")
                .city("Chicago")
                .user(user1)
                .build();
    }

    @Test
    void testEquals_Reflexivity() {
        assertEquals(address1, address1);
    }

    @Test
    void testEquals_Symmetry() {
        assertEquals(address1, address2);
        assertEquals(address2, address1);
    }

    @Test
    void testEquals_Transitivity() {
        Address address4 = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("New York")
                .user(new User()) // Completely different user
                .build();
        
        assertEquals(address1, address2);
        assertEquals(address2, address4);
        assertEquals(address1, address4);
    }

    @Test
    void testEquals_Consistency() {
        // Multiple calls should return same result
        assertEquals(address1, address2);
        assertEquals(address1, address2);
        assertNotEquals(address1, address3);
        assertNotEquals(address1, address3);
    }

    @Test
    void testEquals_NullComparison() {
        assertNotEquals(null, address1);
    }

    @Test
    void testEquals_DifferentClass() {
        assertNotEquals(address1, new Object());
    }

    @Test
    void testEquals_DifferentAddressId() {
        Address differentId = Address.builder()
                .addressId(999)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("New York")
                .build();
        assertNotEquals(address1, differentId);
    }

    @Test
    void testEquals_DifferentFullAddress() {
        Address differentAddress = Address.builder()
                .addressId(1)
                .fullAddress("Different Address")
                .postalCode("12345")
                .city("New York")
                .build();
        assertNotEquals(address1, differentAddress);
    }

    @Test
    void testEquals_DifferentPostalCode() {
        Address differentPostalCode = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("99999")
                .city("New York")
                .build();
        assertNotEquals(address1, differentPostalCode);
    }

    @Test
    void testEquals_DifferentCity() {
        Address differentCity = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("Los Angeles")
                .build();
        assertNotEquals(address1, differentCity);
    }

    @Test
    void testEquals_UserFieldExcluded() {
        Address completelyDifferentUser = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("New York")
                .user(User.builder() // Completely different user
                        .userId(999)
                        .firstName("Different")
                        .lastName("User")
                        .build())
                .build();
        
        assertEquals(address1, completelyDifferentUser); // User no debería afectar equals
    }

    @Test
    void testEquals_NullFields() {
        Address nullFields = Address.builder()
                .addressId(null)
                .fullAddress(null)
                .postalCode(null)
                .city(null)
                .build();
        
        Address alsoNullFields = Address.builder()
                .addressId(null)
                .fullAddress(null)
                .postalCode(null)
                .city(null)
                .build();
        
        assertEquals(nullFields, alsoNullFields);
        assertNotEquals(address1, nullFields);
    }

    @Test
    void testEquals_SameValuesDifferentUserInstance() {
        User sameUserDifferentInstance = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .build();
        
        Address addressWithSameUser = Address.builder()
                .addressId(1)
                .fullAddress("123 Main St, Apt 4B")
                .postalCode("12345")
                .city("New York")
                .user(sameUserDifferentInstance)
                .build();
        
        assertEquals(address1, addressWithSameUser);
    }
}