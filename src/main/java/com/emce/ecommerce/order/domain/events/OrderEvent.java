package com.emce.ecommerce.order.domain.events;

import com.emce.ecommerce.order.domain.entity.Order;

public class OrderEvent {
    private final Order order;
    private final EventType type;

    public OrderEvent(Order order, EventType type) {
        this.order = order;
        this.type = type;
    }

    public Order getOrder() {
        return order;
    }

    public EventType getType() {
        return type;
    }
}