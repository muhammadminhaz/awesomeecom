package com.muhammadminhaz.orderservice.client;

import com.muhammadminhaz.orderservice.dto.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CustomerClient {
    private final WebClient webClient = WebClient.create("http://customer-service:4000");

    public Mono<CustomerResponseDTO> getCustomer(String customerId) {
        return webClient.get()
                .uri("/customer/{customerId}", customerId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Customer service returned error: " + response.statusCode()))
                )
                .bodyToMono(CustomerResponseDTO.class)
                .timeout(Duration.ofSeconds(20))
                .retry(2);
    }
}
