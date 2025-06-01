package com.duikt.fittrack.IntegrationTests;

import com.duikt.fittrack.config.TestDataInitializer;
import com.duikt.fittrack.domain.ProgressTrackerDTO;
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
public class ProgressTrackerRestControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String token;
    private UUID progressTrackerId;
    private UUID regularUserId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        progressTrackerId = testData.progressTrackerId();
        regularUserId = testData.regularUserId();
        token = testData.userToken();
    }

    @Test
    void shouldGetProgressEntries() {
        webTestClient.get()
                .uri("/api/v1/progress")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProgressTrackerDTO.class)
                .consumeWith(response -> {
                    List<ProgressTrackerDTO> entries = response.getResponseBody();
                    assertNotNull(entries);
                    assert(!entries.isEmpty());
                });
    }

    @Test
    void shouldGetProgressEntryById() {
        webTestClient.get()
                .uri("/api/v1/progress/{id}", progressTrackerId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProgressTrackerDTO.class)
                .consumeWith(response -> {
                    ProgressTrackerDTO entry = response.getResponseBody();
                    assertNotNull(entry);
                    assertEquals(progressTrackerId, entry.getId());
                });
    }

    @Test
    void shouldCreateProgressEntry() {
        ProgressTrackerDTO newEntry = ProgressTrackerDTO.builder()
                .date(LocalDate.now())
                .weight(75.0)
                .fatPercentage(18.0)
                .muscleMass(50.0)
                .build();
        ProgressTrackerDTO createdEntry = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/progress")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newEntry)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProgressTrackerDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdEntry);
        assertNotNull(createdEntry.getId());
        assertEquals(75.0, createdEntry.getWeight());
        assertEquals(18.0, createdEntry.getFatPercentage());
    }

    @Test
    void shouldUpdateProgressEntry() {
        ProgressTrackerDTO updatedEntry = ProgressTrackerDTO.builder()
                .id(progressTrackerId)
                .date(LocalDate.now())
                .weight(80.0)
                .fatPercentage(16.5)
                .muscleMass(52.0)
                .build();
        ProgressTrackerDTO resultEntry = webTestClient.put()
                .uri("/api/v1/progress/{id}", progressTrackerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEntry)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProgressTrackerDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(resultEntry);
        assertEquals(80.0, resultEntry.getWeight());
        assertEquals(16.5, resultEntry.getFatPercentage());
    }

    @Test
    void shouldDeleteProgressEntry() {
        ProgressTrackerDTO entryToDelete = ProgressTrackerDTO.builder()
                .date(LocalDate.now())
                .weight(70.0)
                .fatPercentage(20.0)
                .muscleMass(48.0)
                .build();
        ProgressTrackerDTO createdEntry = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/progress")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(entryToDelete)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProgressTrackerDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdEntry);
        UUID createdEntryId = createdEntry.getId();
        webTestClient.delete()
                .uri("/api/v1/progress/{id}", createdEntryId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();
        webTestClient.get()
                .uri("/api/v1/progress/{id}", createdEntryId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }
}
