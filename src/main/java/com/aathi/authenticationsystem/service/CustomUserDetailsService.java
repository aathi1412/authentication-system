package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.entity.User;
import com.aathi.authenticationsystem.exception.InvalidCredentialsException;
import com.aathi.authenticationsystem.repository.UserRepository;
import com.aathi.authenticationsystem.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password"));
        return CustomUserDetails.builder()
                .user(user)
                .build();
    }
}
