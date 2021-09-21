package com.vertx.course.mutiny.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {

        Vertx.vertx()
                .deployVerticle(new MainVerticle())
                .subscribe().with(
                        id -> LOG.info("Success deployed: {}", id),
                        failure -> LOG.info("Something wrong happened.", failure)
                );

    }

    @Override
    public Uni<Void> asyncStart() {

        var router = Router.router(vertx);
        router.route().failureHandler(failureHandler -> {
            failureHandler.response()
                    .setStatusCode(500)
                    .endAndForget("Something went wrong!");
        });
        router.get("/users").respond(routingContext -> {
            final var body = new JsonArray()
                    .add(new JsonObject().put("name", "alice"))
                    .add(new JsonObject().put("name", "alice"));

            return Uni.createFrom()
                    .item(body);
        });

        return vertx.createHttpServer()
                .requestHandler(req -> req.response().endAndForget("Hello!"))
                .requestHandler(router)
                .listen(8080)
                .replaceWithVoid();
    }
}
