package com.vertx.course.quarkus;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.stream.Collectors;

@Path("/vertx/users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UsersResourceVertx {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersResourceVertx.class);

    private final WebClient webClient;

    private final Vertx vertx;

    @Inject
    public UsersResourceVertx(Vertx vertx) {
        this.vertx = vertx;
        this.webClient = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost").setDefaultPort(8080));
    }

    @GET
    public Uni<JsonArray> getUsers() {
        LOGGER.info("Get all users...");

        return User.<User>listAll(Sort.by("id"))
                .onItem()
                .transform(listUsers ->
                        new JsonArray(listUsers.stream()
                                .map(user -> new JsonObject()
                                        .put("id", user.id)
                                        .put("name", user.name))
                                .collect(Collectors.toList())));
    }

    @GET
    @Path("/webclient")
    public Uni<JsonArray> getUsersUsingWebClient() {
        LOGGER.info("Get all users...");

        return webClient.get("users")
                .send()
                .onItem()
                .transform(HttpResponse::bodyAsJsonArray);
    }
}

