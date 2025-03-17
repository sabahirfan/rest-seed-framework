package com.dtg.seed.framework;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

/**
 * Base test class providing common setup and configuration for REST API testing.
 * This class handles the configuration of REST-assured with base URL settings
 * that can be customized through environment variables or system properties.
 *
 * <p>Configuration priority:</p>
 * <ol>
 *   <li>Environment variable (TEST_BASE_URL)</li>
 *   <li>System property (test.baseUrl)</li>
 *   <li>Default value (http://localhost:8080)</li>
 * </ol>
 *
 * <p>Examples of valid base URLs:</p>
 * <ul>
 *   <li>Local environment: http://localhost:8080</li>
 *   <li>Dev environment: https://eng-z2-001-api.dev.thealphacloud.com</li>
 * </ul>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

  /**
   * The REST-assured request specification used by test methods.
   */
  protected RequestSpecification requestSpec;

  /**
   * Determines the base URL for API testing based on configuration hierarchy.
   * The base URL should include the protocol, host, and port (if needed).
   *
   * @return the complete base URL to be used for API testing
   */
  private String getBaseUrl() {
    // First check environment variable
    String envUrl = System.getenv("TEST_BASE_URL");
    if (envUrl != null && !envUrl.isEmpty()) {
      return envUrl;
    }

    // Then check system property
    String sysUrl = System.getProperty("test.baseUrl");
    if (sysUrl != null && !sysUrl.isEmpty()) {
      return sysUrl;
    }

    // Fall back to default localhost with port
    return "http://localhost:8080";
  }

  /**
   * Sets up the test environment before running any tests.
   * This method configures REST-assured with the appropriate base URL
   * and enables logging of request and response details when validation fails.
   */
  @BeforeAll
  protected void setUp() {
    this.requestSpec = buildRequestSpec();
  }

  /**
   * Builds the REST-assured request specification with the configured base URL.
   * This method can be overridden by subclasses to add additional configuration.
   *
   * @return the configured request specification
   */
  protected RequestSpecification buildRequestSpec() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    String baseUrl = getBaseUrl();
    System.out.println("Configuring tests with baseUrl: " + baseUrl);

    return new RequestSpecBuilder()
        .setBaseUri(baseUrl)
        .build();
  }
}