package com.dtg.seed.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized configuration for the seed data framework.
 * This class provides global settings that can be applied across all seed data operations.
 *
 * <p>Configuration can be set programmatically or through system properties:
 * <ul>
 *   <li>Response logging: -Dseed.logging=true</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("PMD")
public class SeedFrameworkConfig {

  /**
   * System property name for enabling response logging.
   */
  public static final String RESPONSE_LOGGING_PROPERTY = "seed.logging";
  private static final Logger logger = LoggerFactory.getLogger(SeedFrameworkConfig.class);
  /**
   * Default value for response logging (disabled by default to avoid cluttering logs).
   */
  private static final boolean DEFAULT_RESPONSE_LOGGING = false;

  /**
   * Current state of response logging.
   */
  private static boolean responseLoggingEnabled = loadResponseLoggingConfig();

  /**
   * Private constructor to prevent instantiation.
   */
  private SeedFrameworkConfig() {
    // Utility class, no instantiation
  }

  /**
   * Loads the response logging configuration from system properties.
   *
   * @return true if response logging is enabled, false otherwise
   */
  private static boolean loadResponseLoggingConfig() {
    String property = System.getProperty(RESPONSE_LOGGING_PROPERTY);
    boolean enabled = Boolean.parseBoolean(property);

    if (property != null) {
      logger.info("Seed framework response logging {} via system property",
          enabled ? "enabled" : "disabled");
    }

    return enabled;
  }

  /**
   * Checks if response logging is enabled.
   *
   * @return true if response logging is enabled, false otherwise
   */
  public static boolean isResponseLoggingEnabled() {
    return responseLoggingEnabled;
  }

  /**
   * Enables or disables response logging.
   *
   * @param enabled true to enable response logging, false to disable
   */
  public static void setResponseLoggingEnabled(boolean enabled) {
    responseLoggingEnabled = enabled;
    logger.info("Seed framework response logging {} programmatically",
        enabled ? "enabled" : "disabled");
  }

  /**
   * Resets all configuration to default values.
   */
  public static void resetToDefaults() {
    responseLoggingEnabled = DEFAULT_RESPONSE_LOGGING;
    logger.info("Seed framework configuration reset to defaults");
  }
}