package com.muhammadminhaz.orderservice.client;

import com.muhammadminhaz.orderservice.dto.NotificationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationClient {
    private final WebClient webClient = WebClient.create("http://notification-service:4005");

    public Mono<Void> sendOrderConfirmation(NotificationRequestDTO request) {
        return webClient.post()
                .uri("/notify/order-confirmation")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Notification service returned error: " + response.statusCode()))
                )
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(20))
                .retry(2);
    }
}
