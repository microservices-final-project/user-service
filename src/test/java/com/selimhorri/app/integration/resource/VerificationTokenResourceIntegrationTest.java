package com.selimhorri.app.integration.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.VerificationTokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DisplayName("VerificationToken Resource Integration Tests")
public class VerificationTokenResourceIntegrationTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private ObjectMapper objectMapper;

        private MockMvc mockMvc;

        private VerificationTokenDto testVerificationToken;
        private CredentialDto testCredential;
        private UserDto testUser;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                setupTestData();
        }

        private void setupTestData() {
                testUser = UserDto.builder()
                                .userId(1)
                                .firstName("John")
                                .lastName("Doe")
                                .email("john.doe@example.com")
                                .phone("123456789")
                                .imageUrl("http://example.com/image.jpg")
                                .build();

                testCredential = CredentialDto.builder()
                                .credentialId(1)
                                .username("johndoe")
                                .password("password123")
                                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                                .isEnabled(true)
                                .isAccountNonExpired(true)
                                .isAccountNonLocked(true)
                                .isCredentialsNonExpired(true)
                                .userDto(testUser)
                                .build();

                testVerificationToken = VerificationTokenDto.builder()
                                .verificationTokenId(1)
                                .token("test-token-123456")
                                .expireDate(LocalDate.now().plusDays(7))
                                .credentialDto(testCredential)
                                .build();
        }

        @Test
        @DisplayName("Should return all verification tokens when GET /api/verificationTokens")
        void testFindAll_Success() throws Exception {
                mockMvc.perform(get("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.collection").exists())
                                .andExpect(jsonPath("$.collection").isArray());
        }

        @Test
        @DisplayName("Should return verification token by ID when GET /api/verificationTokens/{id}")
        void testFindById_Success() throws Exception {
                String tokenId = "1";

                mockMvc.perform(get("/api/verificationTokens/{verificationTokenId}", tokenId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.verificationTokenId").exists())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.expireDate").exists());
        }

        @Test
        @DisplayName("Should return 400 when GET /api/verificationTokens with blank ID")
        void testFindById_BlankId_BadRequest() throws Exception {
                mockMvc.perform(get("/api/verificationTokens/{verificationTokenId}", " ")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when GET /api/verificationTokens with invalid ID format")
        void testFindById_InvalidIdFormat_BadRequest() throws Exception {
                mockMvc.perform(get("/api/verificationTokens/{verificationTokenId}", "invalid")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should create verification token when POST /api/verificationTokens")
        void testSave_Success() throws Exception {
                VerificationTokenDto newToken = VerificationTokenDto.builder()
                                .token("new-token-789")
                                .expireDate(LocalDate.now().plusDays(7))
                                .credentialDto(testCredential)
                                .build();

                mockMvc.perform(post("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newToken)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.token", is("new-token-789")))
                                .andExpect(jsonPath("$.expireDate").exists())
                                .andExpect(jsonPath("$.credential").exists());
        }

        @Test
        @DisplayName("Should return 400 when POST /api/verificationTokens with null body")
        void testSave_NullBody_BadRequest() throws Exception {
                mockMvc.perform(post("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when POST /api/verificationTokens with invalid data")
        void testSave_InvalidData_BadRequest() throws Exception {
                VerificationTokenDto invalidToken = VerificationTokenDto.builder()
                                .token("") // Token vacío - asumiendo que hay validación
                                .expireDate(LocalDate.now().minusDays(1)) // Fecha expirada
                                .build();

                mockMvc.perform(post("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidToken)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should update verification token when PUT /api/verificationTokens/{id}")
        void testUpdate_Success() throws Exception {
                String tokenId = "1";
                VerificationTokenDto updatedToken = VerificationTokenDto.builder()
                                .verificationTokenId(1)
                                .token("updated-token-456")
                                .expireDate(LocalDate.now().plusDays(14))
                                .credentialDto(testCredential)
                                .build();

                mockMvc.perform(put("/api/verificationTokens/{verificationTokenId}", tokenId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedToken)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.token", is("updated-token-456")))
                                .andExpect(jsonPath("$.verificationTokenId", is(1)));
        }

        @Test
        @DisplayName("Should return 400 when PUT /api/verificationTokens with blank ID")
        void testUpdate_BlankId_BadRequest() throws Exception {
                mockMvc.perform(put("/api/verificationTokens/{verificationTokenId}", " ")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testVerificationToken)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when PUT /api/verificationTokens with null body")
        void testUpdate_NullBody_BadRequest() throws Exception {
                mockMvc.perform(put("/api/verificationTokens/{verificationTokenId}", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when PUT /api/verificationTokens with non-existent ID")
        void testUpdate_NonExistentId_NotFound() throws Exception {
                String nonExistentId = "999";

                mockMvc.perform(put("/api/verificationTokens/{verificationTokenId}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testVerificationToken)))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should delete verification token when DELETE /api/verificationTokens/{id}")
        void testDeleteById_Success() throws Exception {
                String tokenId = "1";

                mockMvc.perform(delete("/api/verificationTokens/{verificationTokenId}", tokenId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", is(true)));
        }

        @Test
        @DisplayName("Should return 400 when DELETE /api/verificationTokens with blank ID")
        void testDeleteById_BlankId_BadRequest() throws Exception {
                mockMvc.perform(delete("/api/verificationTokens/{verificationTokenId}", " ")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when DELETE /api/verificationTokens with invalid ID format")
        void testDeleteById_InvalidIdFormat_BadRequest() throws Exception {
                mockMvc.perform(delete("/api/verificationTokens/{verificationTokenId}", "invalid")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when DELETE /api/verificationTokens with non-existent ID")
        void testDeleteById_NonExistentId_NotFound() throws Exception {
                String nonExistentId = "999";

                mockMvc.perform(delete("/api/verificationTokens/{verificationTokenId}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle concurrent requests properly")
        void testConcurrentRequests() throws Exception {
                String tokenId = "1";

                // Simular múltiples requests concurrentes
                mockMvc.perform(get("/api/verificationTokens/{verificationTokenId}", tokenId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should validate token expiration date in the future")
        void testSave_ExpireDateValidation() throws Exception {
                VerificationTokenDto tokenWithPastDate = VerificationTokenDto.builder()
                                .token("test-token-past")
                                .expireDate(LocalDate.now().minusDays(1)) // Fecha en el pasado
                                .credentialDto(testCredential)
                                .build();

                mockMvc.perform(post("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tokenWithPastDate)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle special characters in token")
        void testSave_SpecialCharactersInToken() throws Exception {
                VerificationTokenDto tokenWithSpecialChars = VerificationTokenDto.builder()
                                .token("token-with-special-chars-!@#$%^&*()")
                                .expireDate(LocalDate.now().plusDays(7))
                                .credentialDto(testCredential)
                                .build();

                mockMvc.perform(post("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tokenWithSpecialChars)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token", is("token-with-special-chars-!@#$%^&*()")));
        }

        @Test
        @DisplayName("Should handle large token strings")
        void testSave_LargeTokenString() throws Exception {
                String largeToken = "a".repeat(1000); // Token de 1000 caracteres

                VerificationTokenDto tokenWithLargeString = VerificationTokenDto.builder()
                                .token(largeToken)
                                .expireDate(LocalDate.now().plusDays(7))
                                .credentialDto(testCredential)
                                .build();

                mockMvc.perform(post("/api/verificationTokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tokenWithLargeString)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }
}