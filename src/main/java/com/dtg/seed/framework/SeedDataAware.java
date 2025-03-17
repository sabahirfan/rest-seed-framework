package com.dtg.seed.framework;

import io.restassured.specification.RequestSpecification;

/**
 * Interface that must be implemented by test classes that use the {@link SeedData} annotation.
 * This interface provides access to the REST Assured request specification that will be used
 * for seeding data.
 *
 * @see SeedDataExtension
 * @see SeedData
 */
public interface SeedDataAware {
  /**
   * Gets the REST Assured request specification to be used for seeding data.
   *
   * @return The configured request specification
   */
  RequestSpecification getRequestSpec();
} 