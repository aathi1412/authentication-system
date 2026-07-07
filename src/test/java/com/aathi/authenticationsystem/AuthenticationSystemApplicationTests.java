package com.aathi.authenticationsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class AuthenticationSystemApplicationTests {

    @Test
    void addTwoNumbers() {

        int actual = Calculator.add(10, 20);
        int expected = 30;

        assertThat(expected).isEqualTo(actual);
    }

    static class Calculator{
        static int add (int a, int b){
            return a + b;
        }
    }

}
