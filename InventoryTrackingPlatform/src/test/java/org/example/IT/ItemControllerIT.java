package org.example.IT;

import org.example.config.TestDataInitializer;
import org.example.domain.ItemDTO;
import org.example.entity.ItemEntity;
import org.example.repository.NotificationRepository;
import org.example.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OperationRepository operationRepository;

    private String adminToken;
    private UUID itemId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        itemId = testData.itemId();
        adminToken = testData.adminToken();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("user@gmail.com")
                .password("securepassword")
                .roles("ADMIN")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @Test
    void shouldGetAllItems() {
        webTestClient.get()
                .uri("/api/v1/items")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemDTO.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertFalse(response.getResponseBody().isEmpty());
                });
    }

    @Test
    void shouldGetItemById() {
        webTestClient.get()
                .uri("/api/v1/items/{id}", itemId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDTO.class)
                .consumeWith(response -> {
                    ItemDTO item = response.getResponseBody();
                    assertNotNull(item);
                    assertEquals(itemId, item.getItemId());
                });
    }

    @Test
    void shouldFailToGetNonexistentItem() {
        UUID nonexistentItemId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/items/{id}", nonexistentItemId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Item not found with id: " + nonexistentItemId);
    }

    @Test
    void shouldCreateItem() {
        ItemDTO newItem = ItemDTO.builder()
                .name("New Laptop")
                .description("High-performance laptop for business use")
                .category("Electronics")
                .currentQuantity(10)
                .criticalQuantity(4)
                .barcode("987654321")
                .supplier("TechSupplier Ltd.")
                .build();

        webTestClient.post()
                .uri("/api/v1/items")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newItem)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDTO.class)
                .consumeWith(response -> {
                    ItemDTO item = response.getResponseBody();
                    assertNotNull(item);
                    assertEquals("New Laptop", item.getName());
                });
    }

    @Test
    void shouldUpdateItem() {
        ItemDTO updatedItem = ItemDTO.builder()
                .name("Updated Laptop")
                .category("Electronics")
                .description("High-performance laptop for business use")
                .currentQuantity(15)
                .criticalQuantity(5)
                .barcode("123987456")
                .supplier("Updated Supplier")
                .build();

        webTestClient.put()
                .uri("/api/v1/items/{id}", itemId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedItem)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDTO.class)
                .consumeWith(response -> {
                    ItemDTO item = response.getResponseBody();
                    assertNotNull(item);
                    assertEquals("Updated Laptop", item.getName());
                    assertEquals(15, item.getCurrentQuantity());
                });
    }

    @Test
    void shouldDeleteItem() {
        operationRepository.deleteAll();
        notificationRepository.deleteAll();

        webTestClient.delete()
                .uri("/api/v1/items/{id}", itemId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();
    }
}
