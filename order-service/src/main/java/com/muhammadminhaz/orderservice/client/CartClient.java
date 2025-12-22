package com.muhammadminhaz.orderservice.client;

import com.muhammadminhaz.orderservice.dto.CartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CartClient {
    private final WebClient webClient = WebClient.create("http://cart-service:4002");

    public Mono<CartDTO> getCart(String customerId) {
        return webClient.get()
                .uri("/cart/{customerId}", customerId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Cart service returned error: " + response.statusCode()))
                )
                .bodyToMono(CartDTO.class)
                .timeout(Duration.ofSeconds(20))
                .retry(2);
    }

}

