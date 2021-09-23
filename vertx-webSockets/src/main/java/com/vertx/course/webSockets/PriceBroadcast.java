package com.vertx.course.webSockets;

import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriceBroadcast {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  private final Map<String, ServerWebSocket> connectedClients = new HashMap<>();

  private final Vertx vertx;

  public PriceBroadcast(final Vertx vertx) {
    this.vertx = vertx;
    this.vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), id -> {
      LOGGER.info("Push update to {} clients!", connectedClients.size());
      connectedClients.values().forEach(ws -> {
        ws.writeTextMessage(new JsonObject()
          .put("symbol", "AMZN")
          .put("value", new Random().nextInt(100))
          .toString());
      });
    });
  }

  public void register(ServerWebSocket serverWebSocket) {
    connectedClients.put(serverWebSocket.textHandlerID(), serverWebSocket);
  }

  public void unregister(ServerWebSocket serverWebSocket) {
    connectedClients.remove(serverWebSocket.textHandlerID());
  }
}
