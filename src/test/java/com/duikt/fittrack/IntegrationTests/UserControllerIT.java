package com.duikt.fittrack.IntegrationTests;

import com.duikt.fittrack.config.TestDataInitializer;
import com.duikt.fittrack.domain.UserDTO;
import com.duikt.fittrack.domain.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
        adminId = testData.adminUserId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldGetUsers() {
        webTestClient.get()
                .uri("/api/v1/users")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDTO.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertTrue(!response.getResponseBody().isEmpty());
                });
    }

    @Test
    void shouldGetUserById() {
        webTestClient.get()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO user = response.getResponseBody();
                    assertNotNull(user);
                    assertEquals(adminId, user.getId());
                });
    }

    @Test
    void shouldCreateUser() {
        UserDTO newUser = UserDTO.builder()
                .email("newuser@fittrack.com")
                .username("NewUser")
                .gender(Gender.FEMALE)
                .age(28)
                .weight(55)
                .height(165)
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users")
                        .queryParam("password", "newuserpassword")
                        .build())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO createdUser = response.getResponseBody();
                    assertNotNull(createdUser);
                    assertEquals("newuser@fittrack.com", createdUser.getEmail());
                    assertEquals("NewUser", createdUser.getUsername());
                });
    }

    @Test
    void shouldUpdateUserProfile() {
        UserDTO updatedUser = UserDTO.builder()
                .id(adminId)
                .email("updatedadmin@fittrack.com")
                .username("UpdatedAdmin")
                .gender(Gender.MALE)
                .age(35)
                .weight(85)
                .height(180)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO user = response.getResponseBody();
                    assertNotNull(user);
                    assertEquals("updatedadmin@fittrack.com", user.getEmail());
                    assertEquals("UpdatedAdmin", user.getUsername());
                });
    }

    @Test
    void shouldFailToUpdateNonexistentUser() {
        UUID nonexistentUserId = UUID.randomUUID();

        UserDTO updatedUser = UserDTO.builder()
                .id(nonexistentUserId)
                .email("nonexistent@fittrack.com")
                .username("Nonexistent")
                .gender(Gender.MALE)
                .age(40)
                .weight(80)
                .height(175)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", nonexistentUserId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found with id: " + nonexistentUserId);
    }

    @Test
    void shouldFailToUpdateUserWithInvalidEmail() {
        UserDTO invalidUser = UserDTO.builder()
                .id(adminId)
                .email("invalid email")
                .username("InvalidUser")
                .gender(Gender.MALE)
                .age(30)
                .weight(75)
                .height(170)
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid email format");
    }

    @Test
    void shouldUpdateUserPassword() {
        String newPassword = "newStrongPassword";
        String passwordConf = "newStrongPassword";

        webTestClient.put()
                .uri("/api/v1/users/{id}/password", adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"password\":\"" + newPassword + "\", \"passwordConf\":\"" + passwordConf + "\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO user = response.getResponseBody();
                    assertNotNull(user);
                });
    }

    @Test
    void shouldDeleteUser() {
        UserDTO newUser = UserDTO.builder()
                .email("deleteuser@fittrack.com")
                .username("DeleteUser")
                .gender(Gender.FEMALE)
                .age(22)
                .weight(50)
                .height(160)
                .build();

        UserDTO createdUser = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/users")
                        .queryParam("password", "deletepassword")
                        .build())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDTO.class)
                .returnResult().getResponseBody();

        assertNotNull(createdUser);
        UUID createdUserId = createdUser.getId();

        webTestClient.delete()
                .uri("/api/v1/users/{id}", createdUserId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/v1/users/{id}", createdUserId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }
}
