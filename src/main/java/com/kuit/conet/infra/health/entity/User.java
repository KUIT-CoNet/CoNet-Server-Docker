package com.kuit.conet.infra.health.entity;

import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String name;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
