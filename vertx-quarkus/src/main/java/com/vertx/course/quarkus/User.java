package com.vertx.course.quarkus;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
public class User extends PanacheEntity {

    @Column(length = 64, unique = true)
    public String name;

}
