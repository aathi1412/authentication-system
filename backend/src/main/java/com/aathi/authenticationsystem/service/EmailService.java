package com.aathi.authenticationsystem.service;

import com.aathi.authenticationsystem.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public void sendVerificationEmail(User user, String verificationToken){
        try {
            String encodedToken = URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);
            String verificationUrl = backendUrl + "/api/auth/activate-account?token=" + encodedToken;
            String safeName = HtmlUtils.htmlEscape(user.getName() != null ? user.getName() : "there");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Verify your email address");

            String html = """
                <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e5e7eb; border-radius: 8px;">
                    <h2 style="color: #111827; margin-top: 0;">Welcome to SecureAuth, %s!</h2>
                    <p style="color: #4b5563; font-size: 15px; line-height: 1.6;">Thank you for registering. Please click the button below to verify your email address and activate your account:</p>
                    <div style="margin: 28px 0; text-align: center;">
                        <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: 600; display: inline-block;">Verify Email Address</a>
                    </div>
                    <p style="color: #6b7280; font-size: 13px; line-height: 1.5;">If the button doesn't work, copy and paste this link into your browser:<br/><a href="%s" style="color: #2563eb; word-break: break-all;">%s</a></p>
                    <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 24px 0;" />
                    <p style="color: #9ca3af; font-size: 12px; margin-bottom: 0;">If you did not create an account, you can safely ignore this email.</p>
                </div>
            """.formatted(safeName, verificationUrl, verificationUrl, verificationUrl);

            String text = "Hello " + safeName + ",\n\nPlease verify your email by clicking the link below:\n" + verificationUrl + "\n\nIf you did not request this, please ignore this email.";

            helper.setText(text, html);
            mailSender.send(message);
            log.info("Verification email sent to {}", user.getEmail());
        } catch (MessagingException ex) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), ex.getMessage());
            throw new RuntimeException("Could not send verification email", ex);
        }
    }

    public void sendResetToken(User user, String resetToken){
        try {
            String encodedToken = URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
            String resetUrl = frontendUrl + "/auth/reset-password?token=" + encodedToken;
            String safeName = HtmlUtils.htmlEscape(user.getName() != null ? user.getName() : "there");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Reset your password");

            String html = """
                <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e5e7eb; border-radius: 8px;">
                    <h2 style="color: #111827; margin-top: 0;">Password Reset Request</h2>
                    <p style="color: #4b5563; font-size: 15px; line-height: 1.6;">Hello %s,</p>
                    <p style="color: #4b5563; font-size: 15px; line-height: 1.6;">We received a request to reset your password. Click the button below to set a new password (valid for 15 minutes):</p>
                    <div style="margin: 28px 0; text-align: center;">
                        <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: 600; display: inline-block;">Reset Password</a>
                    </div>
                    <p style="color: #6b7280; font-size: 13px; line-height: 1.5;">If the button doesn't work, copy and paste this link into your browser:<br/><a href="%s" style="color: #2563eb; word-break: break-all;">%s</a></p>
                    <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 24px 0;" />
                    <p style="color: #9ca3af; font-size: 12px; margin-bottom: 0;">If you did not request a password reset, you can safely ignore this email.</p>
                </div>
            """.formatted(safeName, resetUrl, resetUrl, resetUrl);

            String text = "Hello " + safeName + ",\n\nPlease reset your password using the link below:\n" + resetUrl + "\n\nThis link is valid for 15 minutes. If you did not request this, please ignore this email.";

            helper.setText(text, html);
            mailSender.send(message);
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (MessagingException ex) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), ex.getMessage());
            throw new RuntimeException("Could not send password reset email", ex);
        }
    }
}
