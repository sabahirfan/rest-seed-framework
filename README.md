# REST Seed Framework

A reusable Java framework for seeding test data through REST APIs using RestAssured.

## Features

- Annotation-based data seeding
- Support for multiple data sets
- Class-level and method-level seeding
- JSON file-based test data
- Automatic REST endpoint posting
- Graceful handling of existing data
- Programmatic seeding option
- Custom exception handling
- Comprehensive error reporting and logging

## Installation

### Maven
```xml
<dependency>
    <groupId>com.dtg.alpha</groupId>
    <artifactId>rest-seed-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'com.dtg.alpha:rest-seed-framework:1.0.0'
```

## Usage

### Basic Usage with Class-level Seeding
```java
@SeedData(
    value = "seed/validation/ref/common/calendars.json",
    endpoint = "/api/calendars",
    dtoClass = CalendarDto.class
)
class CalendarSeedTest extends SeedDataTest {
    @Test
    void verifyCalendarsSeeded() {
        given()
            .spec(requestSpec)
            .when()
            .get("/api/calendars")
            .then()
            .statusCode(200)
            .body("$", hasSize(59));
    }
}
```

### Multiple Data Sets
```java
@SeedDataSets({
    @SeedData(
        value = "seed/currencies.json",
        endpoint = "/api/currencies",
        dtoClass = CurrencyDto.class
    ),
    @SeedData(
        value = "seed/currency-pairs.json",
        endpoint = "/api/currency-pairs",
        dtoClass = CurrencyPairDto.class
    )
})
class ReferenceDataTest extends SeedDataTest {
    // Test methods
}
```

### Method-level Seeding
```java
class CurrencySeedTest extends SeedDataTest {
    @Test
    @SeedData(
        value = "seed/currencies.json",
        endpoint = "/api/currencies",
        dtoClass = CurrencyDto.class
    )
    void verifyCurrenciesSeeded() {
        given()
            .when()
            .get("/api/currencies")
            .then()
            .statusCode(200)
            .body("$", hasSize(20));
    }
}
```

### Programmatic Seeding
```java
class DynamicTest extends SeedDataTest {
    @Test
    void testWithDynamicData() {
        seedData(
            "dynamic/test-data.json",
            "/api/endpoint",
            TestDto.class
        );
        // Test methods
    }
}
```

## Configuration

### Enable Response Logging

1. Using system property:
```bash
-Dseed.logging=true
```

2. Programmatically:
```java
SeedFrameworkConfig.setResponseLoggingEnabled(true);
```

## Best Practices

1. **Data Organization**
   - Keep test data files in a dedicated resource directory
   - Use meaningful file names
   - Group related data files in subdirectories

2. **Test Structure**
   - Extend `SeedDataTest` for consistent behavior
   - Use class-level annotations for shared data
   - Use method-level annotations for test-specific data
   - Handle exceptions appropriately

3. **Error Handling**
   - Use specific exceptions for different error types
   - Log relevant context information
   - Check exception types for specific scenarios

## License

This project is licensed under the MIT License - see the LICENSE file for details.