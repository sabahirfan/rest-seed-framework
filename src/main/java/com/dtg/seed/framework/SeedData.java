package com.dtg.seed.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying test data to be seeded before test execution.
 * This annotation can be used at both class and method level, and can be repeated multiple times
 * to seed different sets of data.
 *
 * <p>Example usage at class level:
 * <pre>
 * {@code
 * @SeedData(
 *     value = "seed/data/currencies.json",
 *     endpoint = "/config/currency",
 *     dtoClass = CurrencyDto.class
 * )
 * class CurrencyTest extends SeedDataTest {
 *     // test methods
 * }
 * }
 * </pre>
 * </p>
 *
 * @see SeedDataSets for using multiple seed data annotations
 * @see SeedDataExtension for the implementation that processes this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(SeedDataSets.class)
public @interface SeedData {
  /**
   * Path to the JSON file containing the seed data.
   */
  String value();

  /**
   * REST endpoint where the seed data should be posted.
   */
  String endpoint();

  /**
   * Class type of the DTO that represents the data structure.
   */
  Class<?> dtoClass();
} 