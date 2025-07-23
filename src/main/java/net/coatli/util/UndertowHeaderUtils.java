package net.coatli.util;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UndertowHeaderUtils {

  /**
   * {@link String} for <em>no-store</em>
   */
  private static final String NO_STORE = "no-store";

  /**
   * {@link String} for <em>no-cache</em>
   */
  private static final String NO_CACHE = "no-cache";

  /**
   * MIME type <strong>application/json</strong> with <strong>UTF-8</strong> charset.
   */
  public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";

  /**
   * Custom header for the <strong>trace unique identifier</strong>.
   */
  public static final String X_TRACE_ID = "X-T";

  /**
   * {@link HttpString} for {@link #X_TRACE_ID}.
   */
  public static final HttpString X_TRACE_ID_HTTPSTRING = new HttpString(X_TRACE_ID);

  public static String getHeader(final HttpServerExchange httpServerExchange,
                                 final String             name) {

    final var headerValues = httpServerExchange
      .getRequestHeaders()
      .get(name);

    return
      null == headerValues
        ? null
        : headerValues
            .getLast()
            .trim();

  }

  public static void putRequestHeader(final HttpServerExchange httpServerExchange,
                                      final String             name,
                                      final Object             value) {

    putResponseHeader(
      httpServerExchange,
      new HttpString(name),
      value);

  }

  public static void putRequestHeader(final HttpServerExchange httpServerExchange,
                                      final HttpString         name,
                                      final Object             value) {

    httpServerExchange
      .getRequestHeaders()
      .put(
        name,
        String
          .valueOf(value)
          .trim());

  }

  public static void putResponseHeader(final HttpServerExchange httpServerExchange,
                                       final String             name,
                                       final Object             value) {

    putResponseHeader(
      httpServerExchange,
      new HttpString(name),
      value);

  }

  public static void putResponseHeader(final HttpServerExchange httpServerExchange,
                                       final HttpString         name,
                                       final Object             value) {

    httpServerExchange
      .getResponseHeaders()
      .put(
        name,
        String
          .valueOf(value)
          .trim());

  }

  public static String retrieveTraceId(final HttpServerExchange exchange) {

    return getHeader(exchange, X_TRACE_ID);

  }

  public static void defaultResponseContentType(final HttpServerExchange exchange) {

    putResponseHeader(
      exchange,
      Headers.CONTENT_TYPE,
      APPLICATION_JSON_UTF8);

  }

  public static void noCache(final HttpServerExchange exchange) {

    putResponseHeader(
      exchange,
      Headers.CACHE_CONTROL,
      NO_STORE);

    putResponseHeader(
      exchange,
      Headers.PRAGMA,
      NO_CACHE);

  }

}
