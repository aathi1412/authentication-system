package com.aathi.authenticationsystem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationSystemApplicationTests {

    @Test
    void applicationSanityCheck() {
        assertThat(AuthenticationSystemApplication.class).isNotNull();
    }
}
