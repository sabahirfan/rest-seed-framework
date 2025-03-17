package com.dtg.seed.framework.sample;

import com.dtg.seed.framework.SeedData;
import com.dtg.seed.framework.SeedDataTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

/**
 * Sample test class demonstrating how to use the seed data framework.
 * This test loads user data from a JSON file before running tests.
 */
@SeedData(
    value = "seed-data/example-users.json",
    endpoint = "/api/users",
    dtoClass = UserDto.class
)
public class UserApiSampleTest extends SeedDataTest {

    /**
     * Test that demonstrates retrieving users that were seeded by the framework.
     */
    @Test
    void shouldRetrieveSeededUsers() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("username", hasItems("testuser1", "testuser2", "testuser3"));
    }
    
    /**
     * Test that demonstrates retrieving a specific user by ID.
     */
    @Test
    void shouldRetrieveUserById() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .pathParam("id", "user1")
        .when()
            .get("/api/users/{id}")
        .then()
            .statusCode(200)
            .body("username", equalTo("testuser1"))
            .body("email", equalTo("testuser1@example.com"));
    }
    
    /**
     * Test that demonstrates programmatic data seeding using the seedData method.
     */
    @Test
    void shouldSeedAdditionalDataProgrammatically() {
        // Programmatically seed additional data
        seedData("seed-data/additional-users.json", "/api/users", UserDto.class);
        
        // Verify the additional data
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("username", hasItems("additional1", "additional2"));
    }
} 