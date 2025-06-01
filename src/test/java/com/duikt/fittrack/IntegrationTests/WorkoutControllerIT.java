package com.duikt.fittrack.IntegrationTests;

import com.duikt.fittrack.config.TestDataInitializer;
import com.duikt.fittrack.domain.WorkoutDTO;
import com.duikt.fittrack.domain.enums.Type;
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkoutControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String token;
    private UUID existingWorkoutId;
    private UUID regularUserId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        existingWorkoutId = testData.workoutId();
        regularUserId = testData.regularUserId();
        token = testData.userToken();
    }

    @Test
    void shouldGetAllWorkouts() {
        webTestClient.get()
                .uri("/api/v1/workouts")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkoutDTO.class)
                .consumeWith(response -> {
                    List<WorkoutDTO> workouts = response.getResponseBody();
                    assertNotNull(workouts);
                    assert(workouts.size() > 0);
                });
    }

    @Test
    void shouldGetWorkoutById() {
        webTestClient.get()
                .uri("/api/v1/workouts/{id}", existingWorkoutId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkoutDTO.class)
                .consumeWith(response -> {
                    WorkoutDTO workout = response.getResponseBody();
                    assertNotNull(workout);
                    assertEquals(existingWorkoutId, workout.getId());
                });
    }

    @Test
    void shouldCreateWorkout() {
        WorkoutDTO newWorkout = WorkoutDTO.builder()
                .type(Type.RUNNING)
                .date(LocalDate.now())
                .duration(60)
                .caloriesBurned(500)
                .build();
        WorkoutDTO createdWorkout = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/workouts")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newWorkout)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(WorkoutDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdWorkout);
        assertNotNull(createdWorkout.getId());
        assertEquals(60, createdWorkout.getDuration());
        assertEquals(500, createdWorkout.getCaloriesBurned());
        assertEquals(Type.RUNNING, createdWorkout.getType());
    }

    @Test
    void shouldUpdateWorkout() {
        WorkoutDTO updatedWorkout = WorkoutDTO.builder()
                .id(existingWorkoutId)
                .type(Type.RUNNING)
                .date(LocalDate.now())
                .duration(90)
                .caloriesBurned(600)
                .build();
        WorkoutDTO resultWorkout = webTestClient.put()
                .uri("/api/v1/workouts/{id}", existingWorkoutId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedWorkout)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkoutDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(resultWorkout);
        assertEquals(90, resultWorkout.getDuration());
        assertEquals(600, resultWorkout.getCaloriesBurned());
    }

    @Test
    void shouldDeleteWorkout() {
        WorkoutDTO workoutToDelete = WorkoutDTO.builder()
                .type(Type.RUNNING)
                .date(LocalDate.now())
                .duration(50)
                .caloriesBurned(450)
                .build();
        WorkoutDTO createdWorkout = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/workouts")
                        .queryParam("userId", regularUserId)
                        .build())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(workoutToDelete)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(WorkoutDTO.class)
                .returnResult().getResponseBody();
        assertNotNull(createdWorkout);
        UUID createdWorkoutId = createdWorkout.getId();
        webTestClient.delete()
                .uri("/api/v1/workouts/{id}", createdWorkoutId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();
        webTestClient.get()
                .uri("/api/v1/workouts/{id}", createdWorkoutId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }
}