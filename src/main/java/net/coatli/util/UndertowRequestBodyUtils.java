package net.coatli.util;

import com.jsoniter.JsonIterator;
import io.undertow.server.HttpServerExchange;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@UtilityClass
public class UndertowRequestBodyUtils {

  private static final int EMPTY_BODY_REQUEST_LENGTH = 0;

  public static <T> Optional<T> deserializeBody(final HttpServerExchange httpServerExchange,
                                                final Class<T>           clazz) {

    return
      receiveBodyAsync(httpServerExchange, clazz)
        .thenApply(Optional::ofNullable)
        .exceptionally(
          (throwable) -> {

            LOGGER.error(
              "Error receiving body async",
              throwable);

            throw new RuntimeException(throwable);

          })
        .join();

  }

  private static <T> CompletableFuture<T> receiveBodyAsync(final HttpServerExchange httpServerExchange,
                                                           final Class<T>           clazz) {

    final var completableFuture = new CompletableFuture<T>();

    if (EMPTY_BODY_REQUEST_LENGTH == httpServerExchange.getRequestContentLength()) {

      UndertowResponseBodyUtils.createBadRequestEmptyBodyResponse(httpServerExchange);

      completableFuture.complete(null);

      return completableFuture;

    }

    final var byteArrayOutputStream = new ByteArrayOutputStream();

    httpServerExchange
      .getRequestReceiver()
      .receivePartialBytes(
        (_httpServerExchange, bytes, last) -> {

          try {

            byteArrayOutputStream.write(bytes);

            if (last) {

              final var byteArray = byteArrayOutputStream.toByteArray();

              completableFuture.complete(JsonIterator.deserialize(byteArray, clazz));

            }

          } catch (final Exception exception) {

            UndertowResponseBodyUtils.createUnprocessableBodyResponse(
              httpServerExchange,
              "Error processing the body",
              exception);

            completableFuture.complete(null);

          }

        },
        (_httpServerExchange, ioException) -> {

          UndertowResponseBodyUtils.createUnprocessableBodyResponse(
            httpServerExchange,
            "Error in the request receiver",
            ioException);

          completableFuture.complete(null);

        });

    return completableFuture;

  }

}
