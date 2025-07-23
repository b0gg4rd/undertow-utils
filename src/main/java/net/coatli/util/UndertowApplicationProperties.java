package net.coatli.util;

import lombok.Getter;

import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Singleton to load the application properties for an Undertow application.
 */
public enum UndertowApplicationProperties {

  /**
   * Singleton instance.
   */
  APPLICATION_PROPERTIES;

  /**
   * Default path to the application properties file.
   */
  private static final String DEFAULT_PATH = "/conf/application.properties";

  /**
   * Pattern to match environment variable placeholders in the format ${VAR_NAME:default_value}.
   */
  private static final Pattern ENV_VAR_PATTERN = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?\\}");

  private static final int ENV_VAR_NAME_GROUP = 1;

  private static final int DEFAULT_VALUE_GROUP = 2;

  /**
   * Shortcut for getInstance().getProperty(key).
   * @param key Property key to retrieve the value for.
   * @return The value associated with the specified key, or null if the key does not exist.
   */
  public String get(final String key) {

    return instance.getProperty(key);

  }

  @Getter
  private final Properties instance = initialize();

  private Properties initialize() {

    try (final var inputStream = UndertowApplicationProperties.class.getResourceAsStream(DEFAULT_PATH)) {

      if (null == inputStream) {

        throw new IllegalStateException(StringTemplate.STR."Could not find application properties file at \{DEFAULT_PATH}");

      }

      final var properties = new Properties();

      properties.load(inputStream);

      return resolveEnvVarsValues(properties);

    } catch (final Exception exception) {

      throw new IllegalStateException("Failed to load application properties", exception);

    }

  }

  private Properties resolveEnvVarsValues(final Properties properties) {

    properties.forEach(
      (key, value) -> properties.setProperty(
        key.toString(),
        Optional
          .ofNullable(value)
          .map(this::resolveEnvVarValue)
          .orElse(null)));

    return properties;

  }

  private String resolveEnvVarValue(final Object value) {

    return
      ENV_VAR_PATTERN
        .matcher(value.toString())
        .replaceAll(match -> {

          final var envVarName = match.group(ENV_VAR_NAME_GROUP);

          final var defaultValue = match.group(DEFAULT_VALUE_GROUP);

          final var envVarValue = System.getenv(envVarName);

          return
            null != envVarValue
              ? envVarValue
              : (null != defaultValue ? defaultValue : null);

        });

  }

}
