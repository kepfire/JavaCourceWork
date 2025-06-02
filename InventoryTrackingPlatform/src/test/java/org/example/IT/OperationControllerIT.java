package org.example.IT;

import org.example.config.TestDataInitializer;
import org.example.domain.OperationDTO;
import org.example.domain.enums.OperationSource;
import org.example.domain.enums.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID userId;
    private UUID itemId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        userId = testData.userId();
        itemId = testData.itemId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldCreateManualOperation() {
        OperationDTO newOperation = OperationDTO.builder()
                .operationType(OperationType.INCOMING)
                .quantity(10)
                .source(OperationSource.MANUAL)
                .userId(userId)
                .itemId(itemId)
                .build();

        webTestClient.post()
                .uri("/api/v1/operations/manual")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newOperation)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OperationDTO.class)
                .consumeWith(response -> {
                    OperationDTO operation = response.getResponseBody();
                    assertNotNull(operation);
                    assertEquals(OperationType.INCOMING, operation.getOperationType());
                    assertEquals(10, operation.getQuantity());
                });
    }

    @Test
    void shouldGetOperationsHistory() {
        webTestClient.get()
                .uri("/api/v1/operations")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OperationDTO.class)
                .consumeWith(response -> {
                    List<OperationDTO> operations = response.getResponseBody();
                    assertNotNull(operations);
                    assertFalse(operations.isEmpty());
                });
    }

    @Test
    void shouldFilterOperationsByType() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/operations")
                        .queryParam("operationType", OperationType.INCOMING)
                        .build())
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OperationDTO.class)
                .consumeWith(response -> {
                    List<OperationDTO> operations = response.getResponseBody();
                    assertNotNull(operations);
                    assertFalse(operations.isEmpty());
                    assertTrue(operations.stream().allMatch(op -> OperationType.INCOMING.equals(op.getOperationType())));
                });
    }
}
