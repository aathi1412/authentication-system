package com.aathi.authenticationsystem.dto.user;

import lombok.Getter;

@Getter
public class UpdateUserRequest {

    private String name;
    private String phone;
    private String bio;
}
