package com.vertx.course;

import com.vertx.course.webSockets.MainVerticle;
import com.vertx.course.webSockets.WebSockerHandler;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestMainVerticle.class);

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext context) {

    vertx.deployVerticle(new MainVerticle(), context.succeeding(id -> context.completeNow()));
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void canConnectToWebSocket(Vertx vertx, VertxTestContext context) {

    var client = vertx.createHttpClient();

    client.webSocket(8080, "localhost", WebSockerHandler.PATH)
      .onFailure(context::failNow)
      .onComplete(context.succeeding(ws -> {
        ws.handler(data -> {
          assertEquals("Connected!", data.toString());
          client.close();
          context.completeNow();
        });
      }));

  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void canReceiveMultipleMessages(Vertx vertx, VertxTestContext context) {
    var client = vertx.createHttpClient();

    final AtomicInteger counter = new AtomicInteger(0);

    client.webSocket(8080, "localhost", WebSockerHandler.PATH)
      .onFailure(context::failNow)
      .onComplete(context.succeeding(ws -> {
        ws.handler(data -> {
          LOGGER.debug("message.... {}", data.toString());
          var currentValue = counter.getAndIncrement();
          if (currentValue >= 5) {
            client.close();
            context.completeNow();
          } else {
            LOGGER.debug("not enought messages yet.... ({}/{})", currentValue, 5);
          }
        });
      }));
  }

}
