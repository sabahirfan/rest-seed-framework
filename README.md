# REST Seed Framework

A reusable framework for seeding test data to REST APIs in Java-based test automation projects.

## Overview

REST Seed Framework provides a streamlined approach to seed data into REST APIs before test execution. It leverages JUnit 5 extensions and REST Assured to make data preparation simple and maintainable.

## Features

- Annotation-based data seeding
- JUnit 5 extension integration
- Configurable logging of responses
- Programmatic data seeding
- Support for classpath resources
- JSON data file handling
- Support for both class-level and method-level data seeding

## Installation

Add the framework to your project as a dependency:

### Gradle

```groovy
dependencies {
    testImplementation 'com.dtg.alpha:rest-seed-framework:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.dtg.alpha</groupId>
    <artifactId>rest-seed-framework</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## Usage

### Basic Usage

1. Create a DTO class that matches your API data structure:

```java
@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    // Other fields...
}
```

2. Create a JSON file with test data in your resources directory:

```json
[
  {
    "id": "user1",
    "username": "testuser1",
    "email": "testuser1@example.com"
  },
  {
    "id": "user2",
    "username": "testuser2",
    "email": "testuser2@example.com"
  }
]
```

3. Create a test class that extends `SeedDataTest`:

```java
@SeedData(
    value = "seed-data/users.json",
    endpoint = "/api/users",
    dtoClass = UserDto.class
)
public class UserApiTest extends SeedDataTest {

    @Test
    void shouldRetrieveSeededUsers() {
        given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("username", hasItems("testuser1", "testuser2"));
    }
}
```

### Multiple Data Sets

You can use multiple `@SeedData` annotations on a class:

```java
@SeedData(
    value = "seed-data/users.json",
    endpoint = "/api/users",
    dtoClass = UserDto.class
)
@SeedData(
    value = "seed-data/roles.json",
    endpoint = "/api/roles",
    dtoClass = RoleDto.class
)
public class UserRoleTest extends SeedDataTest {
    // Test methods...
}
```

### Method-Level Seeding

You can also apply the annotation to individual test methods:

```java
public class DynamicSeedingTest extends SeedDataTest {

    @Test
    @SeedData(
        value = "seed-data/specific-users.json",
        endpoint = "/api/users",
        dtoClass = UserDto.class
    )
    void testWithSpecificUsers() {
        // Test method that works with specific users
    }
}
```

### Programmatic Seeding

For more dynamic scenarios, you can seed data programmatically:

```java
public class ProgrammaticSeedingTest extends SeedDataTest {

    @Test
    void seedDynamically() {
        // Seed data programmatically
        seedData("seed-data/dynamic-users.json", "/api/users", UserDto.class);
        
        // Proceed with test that relies on the seeded data
    }
}
```

### Configuring Response Logging

Enable detailed response logging for debugging:

```java
// In a @BeforeAll method
SeedFrameworkConfig.setResponseLoggingEnabled(true);

// Or via system property
// -Dseed.logging=true
```

## Configuration

The framework supports these configuration options:

1. **Base URL**: Can be set via:
   - Environment variable: `TEST_BASE_URL`
   - System property: `test.baseUrl`
   - Default: `http://localhost:8080`

2. **Response Logging**: Can be enabled via:
   - Programmatically: `SeedFrameworkConfig.setResponseLoggingEnabled(true)`
   - System property: `-Dseed.logging=true`

## Requirements

- Java 17 or newer
- JUnit 5
- REST Assured
- Jackson for JSON processing

## License

This project is licensed under the MIT License - see the LICENSE file for details.