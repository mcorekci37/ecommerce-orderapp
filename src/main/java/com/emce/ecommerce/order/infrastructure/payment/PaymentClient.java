package com.emce.ecommerce.order.infrastructure.payment;

import com.emce.ecommerce.order.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    public ResponseEntity<String> processPayment(Order order) {
//        String paymentProviderUrl = "https://some-payment-provider.com/api/payments";
//        PaymentRequest paymentRequest = new PaymentRequest(order);
//        return restTemplate.postForEntity(paymentProviderUrl, paymentRequest, String.class);
        return ResponseEntity.ok(String.format("Payment processed for order %s", order.getId().getValue()));
    }
    public ResponseEntity<String> withdrawPayment(Order order) {
//        String paymentProviderUrl = "https://some-payment-provider.com/api/payments";
//        PaymentRequest paymentRequest = new PaymentRequest(order);
//        return restTemplate.postForEntity(paymentProviderUrl, paymentRequest, String.class);
        return ResponseEntity.ok(String.format("Payment withdraw for order %s", order.getId().getValue()));
    }
}
