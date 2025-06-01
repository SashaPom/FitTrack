package com.duikt.fittrack.IntegrationTests;

import com.duikt.fittrack.config.TestDataInitializer;
import com.duikt.fittrack.domain.NutritionLogDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NutritionLogRestControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String token;
    private UUID nutritionLogId;
    private UUID regularUserId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        nutritionLogId = testData.nutritionLogId();
        regularUserId = testData.regularUserId();
        token = testData.userToken();
    }

    @Test
    void shouldGetNutritionLogs() {
        webTestClient.get()
                .uri("/api/v1/nutrition")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NutritionLogDTO.class)
                .consumeWith(response -> {
                    List<NutritionLogDTO> logs = response.getResponseBody();
                    assertNotNull(logs);
                    assert(logs.size() > 0);
                });
    }

    @Test
    void shouldGetNutritionLogById() {
        webTestClient.get()
                .uri("/api/v1/nutrition/{id}", nutritionLogId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(NutritionLogDTO.class)
                .consumeWith(response -> {
                    NutritionLogDTO log = response.getResponseBody();
                    assertNotNull(log);
                    assertEquals(nutritionLogId, log.getId());
                });
    }

    @Test
    void shouldCreateNutritionLog() {
        NutritionLogDTO newLog = NutritionLogDTO.builder()
                .date(LocalDate.now())
                .totalCalories(1800)
                .build();
        NutritionLogDTO createdLog = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/nutrition")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newLog)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(NutritionLogDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdLog);
        assertNotNull(createdLog.getId());
        assertEquals(1800, createdLog.getTotalCalories());
    }

    @Test
    void shouldUpdateNutritionLog() {
        NutritionLogDTO updatedLog = NutritionLogDTO.builder()
                .id(nutritionLogId)
                .date(LocalDate.now())
                .totalCalories(2200)
                .build();
        NutritionLogDTO resultLog = webTestClient.put()
                .uri("/api/v1/nutrition/{id}", nutritionLogId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedLog)
                .exchange()
                .expectStatus().isOk()
                .expectBody(NutritionLogDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(resultLog);
        assertEquals(2200, resultLog.getTotalCalories());
    }

    @Test
    void shouldDeleteNutritionLog() {
        NutritionLogDTO logToDelete = NutritionLogDTO.builder()
                .date(LocalDate.now())
                .totalCalories(2000)
                .build();
        NutritionLogDTO createdLog = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/nutrition")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(logToDelete)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(NutritionLogDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdLog);
        UUID createdLogId = createdLog.getId();
        webTestClient.delete()
                .uri("/api/v1/nutrition/{id}", createdLogId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();
        webTestClient.get()
                .uri("/api/v1/nutrition/{id}", createdLogId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }
}
