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
  private static final String ENV_VAR_REGEX = "\\$\\{([^}:]+)(?::([^}]*))?\\}";

  /**
   * Group index for the environment variable name in the regex pattern.
   */
  private static final int ENV_VAR_NAME_GROUP = 1;

  /**
   * Group index for the default value in the regex pattern.
   */
  private static final int DEFAULT_VALUE_GROUP = 2;

  /**
   * Shortcut for getInstance().getProperty(key).
   * @param key Property key to retrieve the value for.
   * @return The value associated with the specified key, or null if the key does not exist.
   */
  public String get(final String key) {

    return instance.getProperty(key);

  }

  /**
   * Properties instance containing the application properties.
   */
  @Getter
  private final Properties instance = initialize();

  /**
   * Initializes the application properties by loading them from the {#DEFAUL_PATH}.
   * @return Properties instance with the loaded and resolved properties.
   */
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

  /**
   * Resolves environment variable placeholders in the properties values.
   * @param properties {@link Properties} instance containing the entries to resolve.
   * @return The same {@link Properties} instance with environment variable placeholders resolved.
   */
  private Properties resolveEnvVarsValues(final Properties properties) {

    final var pattern = Pattern.compile(ENV_VAR_REGEX);

    properties.forEach(
      (key, value) -> properties.setProperty(
        key.toString(),
        Optional
          .ofNullable(value)
          .map(v -> resolveEnvVarValue(v, pattern))
          .orElse(null)));

    return properties;

  }

  /**
   * Resolves the value of an environment variable placeholder.
   * If the environment variable is not set, it returns the default value if provided.
   * @param value The value containing the environment variable placeholder.
   * @return The resolved value of the environment variable or the default value.
   */
  private String resolveEnvVarValue(final Object  value,
                                    final Pattern pattern) {

    return
      pattern
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
