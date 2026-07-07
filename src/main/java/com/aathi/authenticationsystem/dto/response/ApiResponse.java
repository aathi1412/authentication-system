package com.aathi.authenticationsystem.dto.response;

import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private Instant timeStamp;
    private int status;
    private String error;
    private String message;
}
