package com.vertx.course.quarkus;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UsersResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersResource.class);

    @GET
    public Uni<List<User>> getUsers() {
        LOGGER.info("Get all users...");
        return User.listAll(Sort.by("id"));
    }

    @GET
    @Path("/{id}")
    public Uni<User> getById(Long id) {
        LOGGER.info("Get by id: {}", id);
        return User.findById(id);
    }

    @POST
    public Uni<Response> create(User user) {
        LOGGER.info("Create: {}", user);

        return Panache.<User>withTransaction(user::persist)
                .onItem().transform(item -> Response.created(URI.create("/users/" + item.id)).build());

    }
}

