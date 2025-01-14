package com.emce.ecommerce.order.infrastructure.kafka;

import com.emce.ecommerce.order.domain.events.OrderEvent;

public interface EventPublisher {
    void publish(OrderEvent event);
}