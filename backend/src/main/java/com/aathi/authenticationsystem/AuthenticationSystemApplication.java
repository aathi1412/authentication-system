package com.aathi.authenticationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AuthenticationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationSystemApplication.class, args);
    }

}
