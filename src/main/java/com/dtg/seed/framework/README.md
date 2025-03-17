# Data Seeding Framework

## Summary
The Data Seeding Framework is a robust, annotation-driven solution for managing test data in functional testing environments. It provides a standardized approach to seed reference and configuration data into test environments through REST APIs. The framework simplifies the process of setting up consistent test data across development, testing, and CI/CD pipelines, ensuring reliable and reproducible test results.

## CI/CD Integration
The framework is designed to seamlessly integrate with CI/CD pipelines, allowing for:
- Automated test environment preparation with consistent reference data
- Parallel test execution with isolated data sets
- Reproducible test results across different environments
- Simplified test setup through the dedicated Gradle task `gradlew seedDataTest`
- Consistent test data management across development, QA, and production-like environments

## Overview
The seeding framework consists of several components that work together to provide a seamless test data setup experience:
```
seed/framework
├── SeedData.java                       - Main annotation for declaring test data
├── SeedDataSets.java                   - Container for multiple SeedData annotations
├── SeedDataAware.java                  - Interface for test classes to provide REST configuration
├── SeedDataLoader.java                 - Core component that loads and posts data using RestAssured
├── SeedDataExtension.java              - JUnit extension that processes annotations
├── SeedDataException.java              - Custom exception for data loading/processing errors
├── SeedDataConfigurationException.java - Custom exception for configuration/setup errors
└── SeedDataTest.java                   - Base class for tests requiring data seeding
```
## Features
- Annotation-based data seeding
- Support for multiple data sets
- Class-level and method-level seeding
- JSON file-based test data
- Automatic REST endpoint posting
- Graceful handling of existing data
- Programmatic seeding option
- Custom exception handling for better error diagnosis
- Comprehensive error reporting and logging
## Usage
### Basic Usage Seed Data at class level
```java
@SeedData(
        value = "seed/validation/ref/common/calendars.json",
        endpoint = "/api/config/calendar",
        dtoClass = CalendarDto.class
)
class CalendarSeedTest extends SeedDataTest {

   @Test
   void verifyCalendarsSeeded() {
      // Verify the calendars were seeded correctly
      given()
              .spec(requestSpec)
              .when()
              .get("/api/config/calendars")
              .then()
              .statusCode(200)
              .body("$", hasSize(59));
   }
}
```
### Multiple Data Sets on class level
The seeding will happen in before all callback in this scenario.
```java
@SeedDataSets({
        @SeedData(
                value = "seed/validation/ref/common/currencies.json",
                endpoint = "/api/config/currency",
                dtoClass = CurrencyDto.class
        ),
        @SeedData(
                value = "seed/validation/ref/common/currency-pairs.json",
                endpoint = "/api/config/currencypair",
                dtoClass = CurrencyPairDto.class
        ),
})
class ReferenceDataTest extends BaseTest {
    // Test methods
}
```

### Seed Data on test method level
The seeding will happen in before each method callback in this scenario.
```java
class CurrencySeedTest extends SeedDataTest {

   @Test
   @SeedData(
           value = "seed/validation/ref/common/currencies.json",
           endpoint = "/api/config/currency",
           dtoClass = CurrencyDto.class
   )
   void verifyCurrenciesSeeded() {
      // Verify the currencies were seeded correctly
      given()
              .when()
              .get("/api/config/currencies")
              .then()
              .statusCode(200)
              .body("$", hasSize(20));
   }

   @Test
   @SeedData(
           value = "seed/validation/ref/common/currency-pairs.json",
           endpoint = "/api/config/currencypair",
           dtoClass = CurrencyPairDto.class
   )
   void verifyCurrencyPairsSeeded() {
      // Verify the currency pairs were seeded correctly
      given()
              .when()
              .get("/api/config/currencypairs")
              .then()
              .statusCode(200)
              .body("$.size()", is(greaterThanOrEqualTo(29)));  }
}

```
### Programmatic Seeding
```java
class DynamicTest extends ReferenceDataTest {
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

### How to run Seed Data Tests
Run tests annotated with `@SeedData` or `@SeedDataSets` can be executed using this gradle task:
```shell
  gradlew seedDataTest
```

## Enabling Response Logging for Seed Data Tests
You can enable response logging when running seed data tests in several ways:

1. Using the Gradle project property (recommended):
```shell
  gradlew seedDataTest -PenableSeedLogging
```

2. Using the system property directly:
```shell
  gradlew seedDataTest -Dseed.logging=true
```
This can be added to your JVM arguments when running tests.
### 3. Programmatically
You can enable or disable response logging programmatically:
```java
// Enable response logging
SeedFrameworkConfig.setResponseLoggingEnabled(true);
// Disable response logging
SeedFrameworkConfig.setResponseLoggingEnabled(false);
```
This is useful for enabling logging only for specific test classes or methods.

### Log Output
When response logging is enabled, you'll see detailed DEBUG-level logs for each request/response:
```
INFO c.c.o.f.s.f.SeedDataLoader - ---------- Response Details [SUCCESS] ----------
INFO c.c.o.f.s.f.SeedDataLoader - DTO Type: CurrencyDto
INFO c.c.o.f.s.f.SeedDataLoader - Status Code: 201
INFO c.c.o.f.s.f.SeedDataLoader - Status Line: Created
INFO c.c.o.f.s.f.SeedDataLoader - Headers: {Content-Type=application/json, ...}
INFO c.c.o.f.s.f.SeedDataLoader - Request Body: {"code":"USD","name":"US Dollar"}
INFO c.c.o.f.s.f.SeedDataLoader - Response Body: {"id":1,"code":"USD","name":"US Dollar"}
INFO c.c.o.f.s.f.SeedDataLoader - ----------------------------------------------
```

## Framework Components
### SeedData Annotation
- Specify the test data file, endpoint, and DTO class
- Can be used at class or method level
- Repeatable for multiple data sets
### SeedDataLoader
- Read JSON files from classpath
- Deserializes to specified DTO types
- Posts data to REST endpoints
- Handles HTTP responses (200, 201, 400)
- Throws `SeedDataException` for data loading/processing errors
### SeedDataExtension
- JUnit Jupiter extension
- Processes annotations before test execution
- Supports both class and method level annotations
- Validates test class implementation
- Throws appropriate exceptions for configuration and data issues
### Custom Exceptions
#### SeedDataException
- Thrown for data loading and processing errors
- Provides detailed error messages with context
- Includes original cause for debugging
- Examples: JSON parsing errors, HTTP request failures
#### SeedDataConfigurationException
- Thrown for configuration and setup issues
- Indicates missing or invalid test setup
- Examples: missing implementations, invalid RequestSpec
### SeedDataTest
- Base class for tests requiring data seeding
- Provides REST Assured configuration
- Offers programmatic seeding methods
- Handles exception propagation
## Best Practices
1. **Data Organization**
   - Keep test data files in a dedicated resource directory
   - Use meaningful file names that reflect the data content
   - Group related data files in subdirectories
2. **Test Structure**
   - Extend `SeedDataTest` for consistent behavior
   - Use class-level annotations for shared data
   - Use method-level annotations for test-specific data
   - Handle exceptions appropriately in test methods
3. **Error Handling**
   - The Framework provides specific exceptions for different error types
   - Use try-catch blocks for recoverable errors
   - Log relevant context information
   - Check exception types for specific error scenarios
4. **Performance**
   - Data is seeded only once for class-level annotations
   - Method-level data is seeded before each test
   - Existing data checks prevent duplicate entries
   - Efficient error handling reduces overhead
## Common Issues and Solutions
1. **Missing Data File**
   ```
   SeedDataException: Failed to load seed data from...
   ```
   - Verify file path is correct
   - Ensure JSON file is in classpath
   - Check file permissions
   - Review stack trace for root cause
2. **Invalid JSON Format**
   ```
   SeedDataException: Failed to parse JSON data...
   ```
   - Validate JSON syntax
   - Ensure array structure
   - Check DTO field names match
   - Use JSON validator tools
3. **Configuration Issues**
   ```
   SeedDataConfigurationException: Test class must implement SeedDataAware...
   ```
   - Verify class implements required interfaces
   - Check RequestSpecification setup
   - Ensure proper test class inheritance
   - Review framework configuration
4. **Endpoint Issues**
   ```
   SeedDataException: Failed to seed data to endpoint...
   ```
   - Verify endpoint URL
   - Check server is running
   - Confirm endpoint accepts POST
   - Review HTTP response codes
   - Check request payload format 