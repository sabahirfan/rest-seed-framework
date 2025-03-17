package com.dtg.seed.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that enables multiple {@link SeedData} annotations to be used
 * on the same element. This annotation is typically not used directly but rather
 * automatically applied when multiple {@link SeedData} annotations are used.
 *
 * <p>Example of multiple seed data annotations:
 * <pre>
 * {@code
 * @SeedData(value = "currencies.json", endpoint = "/currency", dtoClass = CurrencyDto.class)
 * @SeedData(value = "countries.json", endpoint = "/country", dtoClass = CountryDto.class)
 * class SeedDataTest {
 *     // test methods
 * }
 * }
 * </pre>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SeedDataSets {
  /**
   * Array of {@link SeedData} annotations.
   */
  SeedData[] value();
} 