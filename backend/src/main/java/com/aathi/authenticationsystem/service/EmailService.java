package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String BASE_URL = "http://localhost:8080";

    public void sendVerificationEmail(User user, String verificationToken){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(user.getEmail());
        message.setSubject("verify your Email");
        message.setText("if you click the below link, your account gonna hacked!");
        message.setText(BASE_URL + "/api/auth/email-verification?token=" + verificationToken);

        mailSender.send(message);
    }

    public void sentResetToken(User user, String resetToken){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(user.getEmail());
        message.setSubject("Reset Password");
        message.setText("Click the link to Reset Password");
        message.setText(BASE_URL + "/api/auth/reset-password?token=" + resetToken);

        mailSender.send(message);
    }
}
