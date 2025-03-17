package com.dtg.seed.framework;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit Jupiter extension that handles the processing of {@link SeedData} annotations.
 * This extension runs before test execution and seeds the necessary data based on
 * the annotations present at both class and method levels.
 *
 * <p>The extension supports:
 * <ul>
 *   <li>Class-level data seeding (runs once before all tests)</li>
 *   <li>Method-level data seeding (runs before each test method)</li>
 *   <li>Multiple seed data sets via {@link SeedDataSets}</li>
 *   <li>Validation of test class implementation</li>
 *   <li>Configurable response logging via {@link SeedFrameworkConfig}</li>
 * </ul>
 * </p>
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @ExtendWith(SeedDataExtension.class)
 * @SeedData(value = "test-data.json", endpoint = "/api/data", dtoClass = TestDto.class)
 * class MyTest implements SeedDataAware {
 *     // test methods
 * }
 * }
 * </pre>
 * </p>
 *
 * <p>To enable response logging for all tests:
 * <pre>
 * {@code
 * // In a @BeforeAll method or test setup class
 * SeedFrameworkConfig.setResponseLoggingEnabled(true);
 *
 * // Or via system property
 * -Dseed.logging=true
 * }
 * </pre>
 * </p>
 *
 * @throws SeedDataConfigurationException if there are issues with test configuration or setup
 * @throws SeedDataException if there are issues loading or processing seed data
 * @see SeedData
 * @see SeedDataSets
 * @see SeedDataAware
 * @see SeedFrameworkConfig
 */
public class SeedDataExtension implements BeforeAllCallback, BeforeEachCallback {

  /**
   * Processes class-level seed data annotations before any test methods are run.
   *
   * @param context The extension context
   * @throws SeedDataConfigurationException if there are issues with test configuration
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    context.getTestClass().ifPresent(testClass -> {
      // Handle class-level annotations
      processSeedDataAnnotations(testClass, context);
    });
  }

  /**
   * Processes method-level seed data annotations before each test method.
   *
   * @param context The extension context
   * @throws SeedDataConfigurationException if there are issues with test configuration
   */
  @Override
  public void beforeEach(ExtensionContext context) {
    // Handle method-level annotations
    context.getTestMethod().ifPresent(method -> {
      processSeedDataAnnotations(method, context);
    });
  }

  /**
   * Processes seed data annotations on a given annotated element.
   *
   * @param element The annotated element (class or method)
   * @param context The extension context
   * @throws SeedDataConfigurationException if there are issues with test configuration
   */
  private void processSeedDataAnnotations(java.lang.reflect.AnnotatedElement element, ExtensionContext context) {
    // Handle single @SeedData annotation
    SeedData singleSeed = element.getAnnotation(SeedData.class);
    if (singleSeed != null) {
      processSeedData(singleSeed, context);
    }

    // Handle multiple @SeedData annotations
    SeedDataSets seedDataSets = element.getAnnotation(SeedDataSets.class);
    if (seedDataSets != null) {
      for (SeedData seedData : seedDataSets.value()) {
        processSeedData(seedData, context);
      }
    }
  }

  /**
   * Processes a single seed data annotation by loading and posting the data.
   *
   * @param seedData The seed data annotation to process
   * @param context  The extension context
   * @throws SeedDataConfigurationException if the test instance is not available or does not implement {@link SeedDataAware}
   * @throws SeedDataException              if there are issues loading or processing the seed data
   */
  private void processSeedData(SeedData seedData, ExtensionContext context) {
    Object testInstance = context.getTestInstance()
        .orElseThrow(() -> new SeedDataConfigurationException("Test instance not available"));

    if (!(testInstance instanceof SeedDataAware seedDataAware)) {
      throw new SeedDataConfigurationException(
          "Test class must implement SeedDataAware to use @SeedData annotation"
      );
    }

    RequestSpecification requestSpec = seedDataAware.getRequestSpec();

    if (requestSpec == null) {
      throw new SeedDataConfigurationException("RequestSpecification not available in test instance");
    }

    try {
      SeedDataLoader.loadData(
          seedData.value(),      // JSON file path
          seedData.endpoint(),   // REST endpoint
          seedData.dtoClass(),   // DTO class
          requestSpec           // Request specification
      );
    } catch (Exception e) {
      throw new SeedDataException(
          String.format(
              "Failed to seed data from '%s' to endpoint '%s': %s",
              seedData.value(),
              seedData.endpoint(),
              e.getMessage()
          ),
          e
      );
    }
  }
}