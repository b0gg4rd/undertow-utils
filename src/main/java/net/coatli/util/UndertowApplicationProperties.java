package net.coatli.util;

import lombok.Getter;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
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

    return
      Optional
        .ofNullable(UndertowApplicationProperties.class.getResourceAsStream(DEFAULT_PATH))
        .map(load(new Properties()))
        .map(resolveEnvVarsValues())
        .orElseThrow(() ->new IllegalStateException(StringTemplate.STR."Could not find application properties file at \{DEFAULT_PATH}"));

  }

  /**
   * Loads properties from the given {@link InputStream} into the provided {@link Properties} instance.
   * @param properties {@link Properties} instance to load the entries into.
   * @return {@link Function} that takes an {@link InputStream} and returns the loaded {@link Properties} instance.
   */
  private Function<InputStream, Properties> load(final Properties properties) {

    return
      (inputStream) -> {

        try (inputStream) {

          properties.load(inputStream);

          return properties;

        } catch (final Exception exception) {

          throw new IllegalStateException("Failed to load application properties", exception);

        }

      };

  }

  /**
   * Resolves environment variable placeholders in the properties values.
   * @return {@link Function} that takes a {@link Properties} instance to resolve the environment variable placeholders
   * and return the same {@link Properties} instance.
   */
  private Function<Properties, Properties> resolveEnvVarsValues() {

    return
      (properties) -> {

        properties.forEach(
          (key, value) -> properties.setProperty(
            key.toString(),
            Optional
              .ofNullable(value)
              .map(resolveEnvVarValue())
              .orElse(null)));

        return properties;

      };

  }

  /**
   * Resolves environment variable placeholders in a given value.
   * @return {@link Function} that takes an {@link Object} value and returns the resolved {@link String} value.
   */
  private Function<Object, String> resolveEnvVarValue() {

    return
      value -> Pattern
        .compile(ENV_VAR_REGEX)
        .matcher(value.toString())
        .replaceAll(match -> Optional
          .ofNullable(System.getenv(match.group(ENV_VAR_NAME_GROUP)))
          .orElse(match.group(DEFAULT_VALUE_GROUP)));

  }

}
