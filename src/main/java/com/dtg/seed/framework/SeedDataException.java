package com.dtg.seed.framework;

/**
 * Custom exception class for handling seed data related errors.
 * This exception is thrown when there are issues with loading, processing,
 * or seeding test data.
 */
@SuppressWarnings("PMD")
public class SeedDataException extends RuntimeException {

  /**
   * Constructs a new SeedDataException with the specified detail message.
   *
   * @param message the detail message
   */
  public SeedDataException(String message) {
    super(message);
  }

  /**
   * Constructs a new SeedDataException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public SeedDataException(String message, Throwable cause) {
    super(message, cause);
  }
}