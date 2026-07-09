package com.aathi.authenticationsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email")
    private String email;

    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;

}
