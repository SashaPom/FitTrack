package com.duikt.fittrack.IntegrationTests;

import com.duikt.fittrack.config.TestDataInitializer;
import com.duikt.fittrack.domain.enums.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StatsRestControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String token;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        token = testData.userToken();
    }

    @Test
    void shouldGetWorkoutStatsByType() {
        webTestClient.get()
                .uri("/api/stats/workouts/by-type")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<Type, Map<String, Integer>> stats = response.getResponseBody();
                    assertNotNull(stats);
                    assert(!stats.isEmpty());
                });
    }

    @Test
    void shouldGetCaloriesProgress() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/stats/progress/calories")
                        .queryParam("from", from.toString())
                        .queryParam("to", to.toString())
                        .build())
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<LocalDate, Double> progressStats = response.getResponseBody();
                    assertNotNull(progressStats);
                    assert(!progressStats.isEmpty());
                });
    }
}
