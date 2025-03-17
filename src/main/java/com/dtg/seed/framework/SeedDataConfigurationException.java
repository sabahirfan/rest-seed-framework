package com.dtg.seed.framework;

/**
 * Custom exception class for handling seed data configuration and setup related errors.
 * This exception is thrown when there are issues with test configuration, missing implementations,
 * or invalid setup of seed data components.
 */
@SuppressWarnings("PMD")
public class SeedDataConfigurationException extends RuntimeException {

  /**
   * Constructs a new SeedDataConfigurationException with the specified detail message.
   *
   * @param message the detail message
   */
  public SeedDataConfigurationException(String message) {
    super(message);
  }

  /**
   * Constructs a new SeedDataConfigurationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public SeedDataConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}