package net.coatli.util;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.xnio.Options;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

  public static ExecutorService createHandlerExecutor(final int corePoolSize) {

    return
      new ThreadPoolExecutor(
        corePoolSize,
        corePoolSize * 2,
        KEEP_ALIVE_TIME,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(corePoolSize * 2),
        new ThreadPoolExecutor.CallerRunsPolicy());

  }

  /**
   * Execute the common setup to the handler.
   * @param httpServerExchange Instance of the type {@link HttpServerExchange} for the response's flushing.
   * @return Value of the {@link UndertowHeaderUtils#X_TRACE_ID}.
   */
  public static String setupHandler(final HttpServerExchange httpServerExchange) {

    final var traceId = UndertowHeaderUtils.retrieveTraceId(httpServerExchange);

    ThreadContext.put(UndertowHeaderUtils.X_TRACE_ID, traceId);

    UndertowHeaderUtils.defaultResponseContentType(httpServerExchange);

    UndertowHeaderUtils.noCache(httpServerExchange);

    return traceId;

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
