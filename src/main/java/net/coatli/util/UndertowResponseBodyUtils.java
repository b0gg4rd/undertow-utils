package net.coatli.util;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class UndertowResponseBodyUtils {

  public static void createOkResponse(final HttpServerExchange httpServerExchange,
                                      final String             result) {

    LOGGER.info(
      "[RP_PL] '{}' '{}'",
      StatusCodes.OK,
      result);

    UndertowHeaderUtils.putResponseHeader(
      httpServerExchange,
      Headers.CONTENT_TYPE,
      UndertowHeaderUtils.APPLICATION_JSON_UTF8);

    httpServerExchange
      .setStatusCode(StatusCodes.OK)
      .getResponseSender()
      .send(result);

  }

  public static void createCreatedResponse(final HttpServerExchange httpServerExchange,
                                           final String             headerName,
                                           final String             headerValue) {

    LOGGER.info(
      "[RP_PL] '{}' '{}' '{}'",
      StatusCodes.CREATED,
      headerName,
      headerValue);

    UndertowHeaderUtils.putResponseHeader(
      httpServerExchange,
      headerName,
      headerValue);

    httpServerExchange
      .setStatusCode(StatusCodes.CREATED)
      .endExchange();

  }

  public static void createUnprocessableBodyResponse(final HttpServerExchange httpServerExchange,
                                                     final String             message,
                                                     final Exception          exception) {

    LOGGER.error(
      "[RP_PL] '{}' '{}' '{}'",
      StatusCodes.UNPROCESSABLE_ENTITY,
      message,
      exception.toString(),
      exception);

    UndertowHeaderUtils.putResponseHeader(
      httpServerExchange,
      Headers.CONTENT_TYPE,
      UndertowHeaderUtils.TEXT_PLAIN_UTF8);

    httpServerExchange
      .setStatusCode(StatusCodes.UNPROCESSABLE_ENTITY)
      .endExchange();

  }

  public static void createBadRequestEmptyBodyResponse(final HttpServerExchange httpServerExchange) {

    LOGGER.error(
      "[RP_PL] '{}' Empty body",
      StatusCodes.BAD_REQUEST);

    UndertowHeaderUtils.putResponseHeader(
      httpServerExchange,
      Headers.CONTENT_TYPE,
      UndertowHeaderUtils.TEXT_PLAIN_UTF8);

    httpServerExchange
      .setStatusCode(StatusCodes.BAD_REQUEST)
      .endExchange();

  }

  public static void createInternalServerErrorResponse(final HttpServerExchange httpServerExchange,
                                                       final Throwable          exception) {

    LOGGER.error(
      "[RP_PL] '{}' '{}'",
      StatusCodes.INTERNAL_SERVER_ERROR,
      exception.toString(),
      exception);

    UndertowHeaderUtils.putResponseHeader(
      httpServerExchange,
      Headers.CONTENT_TYPE,
      UndertowHeaderUtils.TEXT_PLAIN_UTF8);

    httpServerExchange
      .setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR)
      .endExchange();

  }

}
