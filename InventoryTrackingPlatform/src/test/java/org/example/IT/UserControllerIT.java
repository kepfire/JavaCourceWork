package org.example.IT;

import org.example.config.TestDataInitializer;
import org.example.domain.UserDTO;
import org.example.domain.enums.Role;
import org.example.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID adminId;


    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        adminId = testData.adminId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldUpdateUserProfile() {
        UserEntity updatedUser = UserEntity.builder()
                .email("updated@example.com")
                .passwordHash("password")
                .username("updatedUser")
                .role(Role.ADMIN)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO user = response.getResponseBody();
                    assertNotNull(user);
                    assertEquals("updated@example.com", user.getEmail());
                    assertEquals("updatedUser", user.getUsername());
                });
    }

    @Test
    void shouldFailToUpdateNonexistentUser() {
        UUID nonexistentUserId = UUID.randomUUID();

        UserEntity updatedUser = UserEntity.builder()
                .email("test@example.com")
                .passwordHash("password")
                .username("testuser")
                .role(Role.ADMIN)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", nonexistentUserId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found with id: " + nonexistentUserId);
    }

    @Test
    void shouldFailToUpdateUserWithInvalidEmail() {
        UserEntity invalidUser = UserEntity.builder()
                .email("invalid email")
                .passwordHash("password")
                .username("testuser")
                .role(Role.ADMIN)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
