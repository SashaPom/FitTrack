package com.duikt.fittrack.IntegrationTests;

import com.duikt.fittrack.config.TestDataInitializer;
import com.duikt.fittrack.domain.GoalDTO;
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
public class GoalRestControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String token;
    private UUID goalId;
    private UUID regularUserId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        goalId = testData.goalId();
        regularUserId = testData.regularUserId();
        token = testData.userToken();
    }

    @Test
    void shouldGetGoals() {
        webTestClient.get()
                .uri("/api/v1/goals")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GoalDTO.class)
                .consumeWith(response -> {
                    List<GoalDTO> goals = response.getResponseBody();
                    assertNotNull(goals);
                    assert(!goals.isEmpty());
                });
    }

    @Test
    void shouldGetGoalById() {
        webTestClient.get()
                .uri("/api/v1/goals/{id}", goalId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GoalDTO.class)
                .consumeWith(response -> {
                    GoalDTO goal = response.getResponseBody();
                    assertNotNull(goal);
                    assertEquals(goalId, goal.getId());
                });
    }

    @Test
    void shouldCreateGoal() {
        GoalDTO newGoal = GoalDTO.builder()
                .name("Build Strength")
                .type("Fitness")
                .targetValue(200)
                .deadline(LocalDate.now().plusDays(45))
                .build();
        GoalDTO createdGoal = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/goals")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newGoal)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GoalDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdGoal);
        assertNotNull(createdGoal.getId());
        assertEquals("Build Strength", createdGoal.getName());
        assertEquals("Fitness", createdGoal.getType());
        assertEquals(200, createdGoal.getTargetValue());
    }

    @Test
    void shouldUpdateGoal() {
        GoalDTO updatedGoal = GoalDTO.builder()
                .id(goalId)
                .name("Improve Stamina Updated")
                .type("Fitness")
                .targetValue(150)
                .deadline(LocalDate.now().plusDays(60))
                .build();
        GoalDTO resultGoal = webTestClient.put()
                .uri("/api/v1/goals/{id}", goalId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedGoal)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GoalDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(resultGoal);
        assertEquals("Improve Stamina Updated", resultGoal.getName());
        assertEquals(150, resultGoal.getTargetValue());
    }

    @Test
    void shouldDeleteGoal() {
        GoalDTO goalToDelete = GoalDTO.builder()
                .name("Temporary Goal")
                .type("Fitness")
                .targetValue(120)
                .deadline(LocalDate.now().plusDays(30))
                .build();
        GoalDTO createdGoal = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/goals")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(goalToDelete)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GoalDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdGoal);
        UUID createdGoalId = createdGoal.getId();
        webTestClient.delete()
                .uri("/api/v1/goals/{id}", createdGoalId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();
        webTestClient.get()
                .uri("/api/v1/goals/{id}", createdGoalId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }
}
