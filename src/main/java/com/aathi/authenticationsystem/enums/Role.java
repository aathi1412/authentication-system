package com.aathi.authenticationsystem.enums;

import lombok.Getter;

import java.util.Set;


@Getter
public enum Role {
    ADMIN(Set.of(Permissions.READ_TODO, Permissions.WRITE_TODO, Permissions.DELETE_TODO)),
    USER(Set.of(Permissions.READ_TODO));

    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions){
        this.permissions = permissions;
    }

}
