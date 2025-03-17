package com.dtg.seed.framework;

import com.crd.otc.functional.BaseTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Base class for reference data functional tests that require data seeding.
 * This class provides the foundation for tests that need to work with seeded data,
 * combining the functionality of {@link BaseTest} with data seeding capabilities.
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic data seeding through {@link SeedData} annotations</li>
 *   <li>Access to REST Assured request specification</li>
 *   <li>Support for both class-level and method-level data seeding</li>
 * </ul>
 * </p>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @SeedData(
 *     value = "seed/validation/ref/common/currencies.json",
 *     endpoint = "/config/currency",
 *     dtoClass = CurrencyDto.class
 * )
 * class CurrencyTest extends SeedDataTest {
 *     @Test
 *     void testCurrencyOperations() {
 *         // Test methods using seeded data
 *     }
 * }
 * }
 * </pre>
 * </p>
 *
 * @see SeedData
 * @see SeedDataExtension
 * @see BaseTest
 */
@ExtendWith(SeedDataExtension.class)
public abstract class SeedDataTest extends BaseTest implements SeedDataAware {

  /**
   * {@inheritDoc}
   * Provides access to the REST Assured request specification for data seeding.
   *
   * @return The configured request specification from the base test
   */
  @Override
  public RequestSpecification getRequestSpec() {
    return buildRequestSpec();
  }

  /**
   * Programmatically seeds data from a JSON file.
   * This method can be used when dynamic data seeding is needed instead of
   * or in addition to annotation-based seeding.
   *
   * @param <T>      The type of DTO representing the data
   * @param jsonPath Path to the JSON file in resources
   * @param endpoint REST endpoint for creating the entities
   * @param dtoClass Class of the DTO
   */
  protected <T> void seedData(String jsonPath, String endpoint, Class<T> dtoClass) {
    SeedDataLoader.loadData(jsonPath, endpoint, dtoClass, requestSpec);
  }
} 