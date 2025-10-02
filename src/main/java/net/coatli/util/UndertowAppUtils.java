package net.coatli.util;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Logger;
import org.xnio.Options;

@UtilityClass
public class UndertowAppUtils {

  private static final String HOST = "host";

  private static final String PORT = "port";

  private static final int BUFFER_SIZE = 1024 * 64;

  private static final int IO_THREADS = Runtime.getRuntime().availableProcessors() * 4;

  private static final int WORKER_THREADS = IO_THREADS * 2;

  private static final int BACKLOG_SIZE = 10000;

  private static final int KEEP_ALIVE_TIME = 200;

  public static void buildAndStart(final Logger      logger,
                                   final Class<?>    clazz,
                                   final HttpHandler routes) {

    logger.info(
      "Starting {} ...",
      clazz.getSimpleName());

    UndertowAppUtils
      .buildUndertow(routes)
      .start();

    logger.info(
      "{} started for interface {} and port {}",
      clazz.getSimpleName(),
      UndertowApplicationProperties.APPLICATION_PROPERTIES.get(HOST),
      UndertowApplicationProperties.APPLICATION_PROPERTIES.get(PORT));

  }

  public static boolean shouldDispatchToWorkerThread(final HttpServerExchange httpServerExchange,
                                                     final HttpHandler        httpHandler) {

    if (httpServerExchange.isInIoThread()) {

      httpServerExchange.dispatch(httpHandler);
      return true;

    }

    return false;

  }

  private static Undertow buildUndertow(final HttpHandler routes) {

    return
      Undertow
        .builder()
        .addHttpListener(
          Integer.parseInt(UndertowApplicationProperties.APPLICATION_PROPERTIES.get(PORT)),
          UndertowApplicationProperties.APPLICATION_PROPERTIES.get(HOST))
        .setBufferSize(BUFFER_SIZE)
        .setIoThreads(IO_THREADS)
        .setWorkerThreads(WORKER_THREADS)
        .setSocketOption(Options.BACKLOG, BACKLOG_SIZE)
        .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false)
        .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
        .setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, false)
        .setHandler(routes)
        .build();

  }

}
