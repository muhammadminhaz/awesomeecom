package com.muhammadminhaz.notificationservice.service;

import com.muhammadminhaz.notificationservice.dto.EmailRequest;
import com.muhammadminhaz.notificationservice.dto.EmailResponse;
import com.muhammadminhaz.notificationservice.dto.OrderConfirmationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from-email}")
    private String fromEmail;

    @Value("${mail.from-name}")
    private String fromName;

    public EmailResponse sendEmail(EmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());

            helper.setText(request.getBody(), request.isHtml());

            mailSender.send(message);

            logger.info("Email sent successfully to {}", request.getTo());

            return new EmailResponse(
                    null,          // JavaMail doesn’t return an ID
                    "sent",
                    request.getTo()
            );

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            logger.error("Unexpected email error", e);
            throw new RuntimeException("Email service error", e);
        }
    }

    public EmailResponse sendOrderConfirmation(OrderConfirmationRequest request) {
        String html = buildOrderConfirmationHtml(request);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(request.getEmail());
        emailRequest.setSubject("Order Confirmation - #" + request.getOrderId());
        emailRequest.setBody(html);
        emailRequest.setHtml(true);

        return sendEmail(emailRequest);
    }

    private String buildOrderConfirmationHtml(OrderConfirmationRequest request) {
        return "<!DOCTYPE html><html><head><style>" +
                "body{font-family:Arial,sans-serif;}" +
                "</style></head>" +
                "<body><div style=\"max-width:600px;margin:0 auto;padding:20px;\">" +
                "<h1 style=\"color:#16a34a;\">Order Confirmed!</h1>" +
                "<p>Hi " + request.getCustomerName() + ",</p>" +
                "<p>Thank you for your order. We're processing it now.</p>" +
                "<div style=\"background:#f3f4f6;padding:15px;border-radius:8px;margin:20px 0;\">" +
                "<p><strong>Order #:</strong> " + request.getOrderId() + "</p>" +
                "<p><strong>Total:</strong> $" + request.getOrderTotal() + "</p>" +
                "<p><strong>Shipping to:</strong> " + request.getShippingAddress() + "</p>" +
                "</div>" +
                "<p>We'll notify you when your order ships.</p>" +
                "<p>Best regards,<br>The AwesomeEcom Team</p>" +
                "</div></body></html>";
    }
}
