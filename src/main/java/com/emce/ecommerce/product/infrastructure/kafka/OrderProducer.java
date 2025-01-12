package com.emce.ecommerce.product.infrastructure.kafka;

import com.emce.ecommerce.order.domain.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private static final String TOPIC = "orders";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper serializer;

    public void publishOrder(Order order) {
        String orderDetails = null;
        try {
            orderDetails = serializer.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            log.error("Order cannot be serialized : {}", order);
        }
        kafkaTemplate.send(TOPIC, orderDetails);
    }
}
