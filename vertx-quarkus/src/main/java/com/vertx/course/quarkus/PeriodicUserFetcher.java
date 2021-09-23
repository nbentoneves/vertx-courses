package com.vertx.course.quarkus;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@ApplicationScoped
public class PeriodicUserFetcher extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicUserFetcher.class);
    public static final String ADDRESS = PeriodicUserFetcher.class.getName();

    @Override
    public Uni<Void> asyncStart() {

        var webClient = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost").setDefaultPort(8080));

        vertx.periodicStream(Duration.ofSeconds(5).toMillis())
                .toMulti()
                .subscribe()
                .with(item -> {
                    LOGGER.info("Periodic fetch");
                    webClient.get("/users")
                            .send()
                            .subscribe().with(result -> {
                                var body = result.bodyAsJsonArray();
                                LOGGER.info("All users from http response: {}", body);
                                vertx.eventBus().publish(ADDRESS, body);
                            });
                });

        return Uni.createFrom().voidItem();
    }
}
