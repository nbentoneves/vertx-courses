package com.vertx.course.webSockets;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSockerHandler implements Handler<ServerWebSocket> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
  private static final String PATH = "/ws/simple/prices";

  @Override
  public void handle(final ServerWebSocket serverWebSocket) {

    if (!PATH.equalsIgnoreCase(serverWebSocket.path())) {
      LOGGER.info("Rejected wrong path: {}", serverWebSocket.path());
      serverWebSocket.writeFinalTextFrame("Wrong path. Only " + PATH + " is accepted!");
      serverWebSocket.close((short) 1000, "Normal closure");
    } else {
      LOGGER.info("Opening web socket connection: {}, {}", serverWebSocket.path(), serverWebSocket.textHandlerID());

      // Accept the connection
      serverWebSocket.accept();
      serverWebSocket.frameHandler(received -> {
        final var message = received.textData();
        LOGGER.info("Received message: {} from client {}", message, serverWebSocket.path());

        if ("disconnect me".equalsIgnoreCase(message)) {
          LOGGER.info("Client close requested!");
          serverWebSocket.close((short) 1000, "Normal closure");
        } else {
          serverWebSocket.writeTextMessage("Not supported => (" + message + ")");
        }
      });
      serverWebSocket.endHandler(onClose -> LOGGER.info("Closed {}", serverWebSocket.textHandlerID()));
      serverWebSocket.exceptionHandler(error -> LOGGER.error("Failed: ", error));
      serverWebSocket.writeTextMessage("Connected!");
    }
  }
}
