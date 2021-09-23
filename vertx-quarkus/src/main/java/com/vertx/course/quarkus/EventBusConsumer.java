package com.vertx.course.quarkus;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventBusConsumer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusConsumer.class);

    @Override
    public Uni<Void> asyncStart() {
        vertx.eventBus().consumer(PeriodicUserFetcher.ADDRESS, message -> {
            LOGGER.info("Consumer from event bus: {}", message.body());
        });

        return Uni.createFrom().voidItem();
    }
}
