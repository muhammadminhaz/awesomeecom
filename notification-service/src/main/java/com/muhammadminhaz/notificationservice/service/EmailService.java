package com.muhammadminhaz.notificationservice.service;

import com.muhammadminhaz.notificationservice.dto.EmailRequest;
import com.muhammadminhaz.notificationservice.dto.EmailResponse;
import com.muhammadminhaz.notificationservice.dto.OrderConfirmationRequest;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${resend.from-name}")
    private String fromName;

    public EmailResponse sendEmail(EmailRequest request) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(request.getTo())
                    .subject(request.getSubject())
                    .html(request.isHtml() ? request.getBody() : null)
                    .text(request.isHtml() ? null : request.getBody())
                    .build();

            CreateEmailResponse response = resend.emails().send(params);

            logger.info("Email sent successfully to {} with ID: {}", request.getTo(), response.getId());
            return new EmailResponse(response.getId(), "sent", request.getTo());

        } catch (ResendException e) {
            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
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
        return "<!DOCTYPE html><html><head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body><div style=\"max-width:600px;margin:0 auto;padding:20px;\">" +
                "<h1 style=\"color:#16a34a;\">Order Confirmed!</h1>" +
                "<p>Hi " + request.getCustomerName() + ",</p>" +
                "<p>Thank you for your order. We're processing it now.</p>" +
                "<div style=\"background:#f3f4f6;padding:15px;border-radius:8px;margin:20px 0;\">" +
                "<p><strong>Order #:</strong> " + request.getOrderId() + "</p>" +
                "<p><strong>Total:</strong> $" + request.getOrderTotal() + "</p>" +
                "<p><strong>Shipping to:</strong> " + request.getShippingAddress() + "</p>" +
                "</div><p>We'll notify you when your order ships.</p>" +
                "<p>Best regards,<br>The AwesomeEcom Team</p>" +
                "</div></body></html>";
    }
}
