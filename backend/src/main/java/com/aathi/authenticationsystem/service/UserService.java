package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void lockOrUnlockAccount(String email){

        userRepository.findByEmail(email)
                .ifPresent(user -> {

                    if(!user.isAccountLocked()){
                        return;
                    }
                    Instant unlockTime = user.getLockTime().plus(15, ChronoUnit.MINUTES);

                    if(Instant.now().isAfter(unlockTime)){
                        user.setAccountLocked(false);
                        user.setFailedAttempts(0);
                        user.setLockTime(null);
                    }


                });

    }

    @Transactional
    public void increaseFailedLoginAttempt(String email){
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.setFailedAttempts(user.getFailedAttempts() + 1);

                    if(user.getFailedAttempts() >= 5){
                        user.setAccountLocked(true);
                        user.setLockTime(Instant.now());
                    }

                });
    }

    @Transactional
    public void resetFailedLoginAttempt(String email){
        userRepository.findByEmail(email)
                .ifPresent(user -> user.setFailedAttempts(0));
    }
}
