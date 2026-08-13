package com.aathi.authenticationsystem.dto.response;

import com.aathi.authenticationsystem.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private String message;
    private UserResponse userResponse;
}
