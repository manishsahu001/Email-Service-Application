package com.springboot.usercrud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.springboot.usercrud.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.hr.email:hr@techcorp.com}")
    private String hrEmail;

    // Send email when new user is created
    public void sendUserCreatedNotification(User user) {
        try {
            // Send to HR Team
            sendHtmlEmail(
                hrEmail,
                "New Employee Created - " + user.getFirstName() + " " + user.getLastName(),
                "user-created",
                prepareUserContext(user, "New Employee Created")
            );

            // Send welcome email to new user
            sendHtmlEmail(
                user.getEmail(),
                "Welcome to Vgrid Solutions - Your Account is Ready",
                "welcome-user",
                prepareUserContext(user, "Welcome to Vgrid Solutions")
            );

        } catch (MessagingException e) {
            System.err.println("Failed to send user creation email: " + e.getMessage());
        }
    }

    // Send email when user is updated
    public void sendUserUpdatedNotification(User user, String changedFields) {
        try {
            Context context = prepareUserContext(user, "Employee Updated");
            context.setVariable("changedFields", changedFields);
            context.setVariable("updateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Send to HR Team
            sendHtmlEmail(
                hrEmail,
                "Employee Updated - " + user.getFirstName() + " " + user.getLastName(),
                "user-updated",
                context
            );

            // Send notification to user
            sendHtmlEmail(
                user.getEmail(),
                "Your Account Has Been Updated",
                "user-updated",
                context
            );

        } catch (MessagingException e) {
            System.err.println("Failed to send user update email: " + e.getMessage());
        }
    }

    // Send email when user is deleted (soft delete)
    public void sendUserDeletedNotification(User user, String reason) {
        try {
            Context context = prepareUserContext(user, "Employee Deactivated");
            context.setVariable("deletionReason", reason);
            context.setVariable("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            sendHtmlEmail(
                hrEmail,
                "Employee Deactivated - " + user.getFirstName() + " " + user.getLastName(),
                "user-deleted",
                context
            );

        } catch (MessagingException e) {
            System.err.println("Failed to send user deletion email: " + e.getMessage());
        }
    }

    // Helper method to send HTML emails
    private void sendHtmlEmail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    // Helper method to prepare user context for templates
    private Context prepareUserContext(User user, String title) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("title", title);
        context.setVariable("companyName", "VRrid Solutions");
        context.setVariable("companyWebsite", "www.vrridsolutions.com");
        context.setVariable("supportEmail", "support@vrridsolutions.com");
        context.setVariable("supportPhone", "+1-555-VRRID");
        context.setVariable("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return context;
    }

    // Simple email method for testing
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
