package com.dtg.seed.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Component responsible for loading and seeding test data from JSON files.
 * This class provides functionality to read JSON data files and post their contents
 * to specified REST endpoints.
 *
 * <p>The loader supports:
 * <ul>
 *   <li>Reading JSON files from the classpath</li>
 *   <li>Deserializing JSON to specified DTO types</li>
 *   <li>Posting data to REST endpoints</li>
 *   <li>Handling existing data gracefully (400 status codes)</li>
 *   <li>Configurable response logging for debugging</li>
 * </ul>
 * </p>
 *
 */
@SuppressWarnings("PMD")
@Component
public class SeedDataLoader {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(SeedDataLoader.class);

  /**
   * Loads data from a JSON file and posts it to the specified endpoint.
   * Uses the global configuration for response logging.
   *
   * @param <T>         The type of DTO representing the data
   * @param jsonPath    Path to the JSON file in the classpath
   * @param endpoint    REST endpoint where the data should be posted
   * @param dtoClass    Class object representing the DTO type
   * @param requestSpec REST Assured request specification to use
   * @throws SeedDataException if loading or posting the data fails
   */
  public static <T> void loadData(String jsonPath, String endpoint, Class<T> dtoClass, RequestSpecification requestSpec) {
    loadData(jsonPath, endpoint, dtoClass, requestSpec, SeedFrameworkConfig.isResponseLoggingEnabled());
  }

  /**
   * Loads data from a JSON file and posts it to the specified endpoint.
   *
   * @param <T>           The type of DTO representing the data
   * @param jsonPath      Path to the JSON file in the classpath
   * @param endpoint      REST endpoint where the data should be posted
   * @param dtoClass      Class object representing the DTO type
   * @param requestSpec   REST Assured request specification to use
   * @param logResponses  Whether to log detailed response information
   * @throws SeedDataException if loading or posting the data fails
   */
  public static <T> void loadData(String jsonPath, String endpoint, Class<T> dtoClass,
                                  RequestSpecification requestSpec, boolean logResponses) {
    try {
      ClassPathResource resource = new ClassPathResource(jsonPath);
      List<T> items = objectMapper.readValue(
          resource.getInputStream(),
          new TypeReference<List<T>>() {
          }
      );
      logger.info("--------------------------------------------------------------");
      logger.info("Seeding data for {} :: {} items from {} to endpoint: {}", dtoClass.getSimpleName(), items.size(), jsonPath, endpoint);

      int successCount = 0;
      int existingCount = 0;
      int failureCount = 0;

      for (T item : items) {
        Response response = given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .body(item)
            .when()
            .post(endpoint)
            .then()
            .statusCode(anyOf(is(201), is(200), is(400))) // 400 if item already exists
            .extract()
            .response();

        int statusCode = response.getStatusCode();
        if (statusCode == 200 || statusCode == 201) {
          successCount++;
          if (logResponses) {
            logResponseDetails(response, dtoClass, "SUCCESS", item);
          }
        } else if (statusCode == 400) {
          existingCount++;
          if (logResponses) {
            logResponseDetails(response, dtoClass, "EXISTING", item);
          }
        } else {
          failureCount++;
          if (logResponses) {
            logResponseDetails(response, dtoClass, "FAILURE", item);
          }
        }
      }

      logger.info("Seeding data completed for {} from {}", dtoClass.getSimpleName(), jsonPath);
      logger.info("Metrics - Total items: {}, Successfully created: {}, Already existing: {}, Failed: {}",
          items.size(), successCount, existingCount, failureCount);
      logger.info("--------------------------------------------------------------");

    } catch (IOException e) {
      logger.error("Failed to load seed data from {} for {}: {}", jsonPath, dtoClass.getSimpleName(), e.getMessage());
      throw new SeedDataException("Failed to load seed data from " + jsonPath, e);
    }
  }

  /**
   * Logs detailed information about a response for debugging purposes.
   *
   * @param response    The REST response to log
   * @param dtoClass    The class of the DTO that was posted
   * @param status      A status label for the log entry
   * @param requestBody The request body that was sent
   */
  private static <T> void logResponseDetails(Response response, Class<T> dtoClass, String status, T requestBody) {
    try {
      String responseBody = response.getBody().asString();
      logger.info("---------- Response Details [{}] ----------", status);
      logger.info("DTO Type: {}", dtoClass.getSimpleName());
      logger.info("Status Code: {}", response.getStatusCode());
      logger.info("Status Line: {}", response.getStatusLine());
      logger.info("Headers: {}", response.getHeaders());
      logger.info("Request Body: {}", objectMapper.writeValueAsString(requestBody));
      logger.info("Response Body: {}", responseBody);
      logger.info("----------------------------------------------");
    } catch (Exception e) {
      logger.warn("Failed to log response details: {}", e.getMessage());
    }
  }
}