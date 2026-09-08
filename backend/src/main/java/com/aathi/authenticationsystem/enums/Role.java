package com.aathi.authenticationsystem.enums;

import lombok.Getter;

import java.util.Set;


@Getter
public enum Role {

    SUPER_ADMIN(Set.of(
            Permissions.ADD_ADMIN,
            Permissions.READ_USERS,
            Permissions.UPDATE_USERS,
            Permissions.DELETE_USERS,
            Permissions.UNLOCK_USERS,
            Permissions.REVOKE_USER_SESSIONS
    )),
    ADMIN(Set.of(
            Permissions.READ_USERS,
            Permissions.UPDATE_USERS,
            Permissions.UNLOCK_USERS,
            Permissions.REVOKE_USER_SESSIONS
    )),
    USER(Set.of());

    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions){
        this.permissions = permissions;
    }

}
