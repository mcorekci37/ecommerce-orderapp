package com.emce.ecommerce.order.infrastructure.kafka;

import com.emce.ecommerce.order.domain.events.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {
    private final OrderProducer producer;

    @Override
    public void publish(OrderEvent event) {
        switch (event.getType()) {
            case CREATE:
                producer.publishCreateEvent(event.getOrder());
                break;
            case CANCEL:
                producer.publishCancelEvent(event.getOrder());
                break;
        }
    }
}