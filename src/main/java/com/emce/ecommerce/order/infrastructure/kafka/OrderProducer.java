package com.emce.ecommerce.order.infrastructure.kafka;

import com.emce.ecommerce.order.domain.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    @Value("${application.kafka.topics.order-creation:order-created}")
    private String ORDER_CREATE_TOPIC;

    @Value("${application.kafka.topics.order-cancellation:order-cancelled}")
    private String ORDER_CANCELED_TOPIC;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper serializer;

    public void publishCreateEvent(Order order) {
        log.info("Order creation will be published for product {} and user {}", order.getProduct(), order.getUsername());
        publishEvent(ORDER_CREATE_TOPIC, order);
        log.info("Order creation is published for product {} and user {}", order.getProduct(), order.getUsername());
    }
    public void publishCancelEvent(Order order) {
        log.info("Order cancellation will be published for product {} and user {}", order.getProduct(), order.getUsername());
        publishEvent(ORDER_CANCELED_TOPIC, order);
        log.info("Order cancellation is published for product {} and user {}", order.getProduct(), order.getUsername());
    }
    public void publishEvent(String topic, Order order) {
        String orderDetails = null;
        try {
            orderDetails = serializer.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            log.error("Order cannot be serialized : {}", order);
            //todo we can apply outbox pattern or DLQ pattern here
        }
        kafkaTemplate.send(topic, orderDetails);
    }
}
