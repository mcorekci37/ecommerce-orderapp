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

    //todo move this to prop file

    private static final String ORDER_CREATE_TOPIC = "order-created";
    private static final String ORDER_CANCELED_TOPIC = "order-canceled";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper serializer;

    public void publishCreateEvent(Order order) {
        publishEvent(ORDER_CREATE_TOPIC, order);
    }
    public void publishCancelEvent(Order order) {
        publishEvent(ORDER_CANCELED_TOPIC, order);
    }
    public void publishEvent(String topic, Order order) {
        String orderDetails = null;
        try {
            orderDetails = serializer.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            log.error("Order cannot be serialized : {}", order);
            //todo we can apply outbox pattern or dlq pattern here
        }
        kafkaTemplate.send(topic, orderDetails);
    }
}
