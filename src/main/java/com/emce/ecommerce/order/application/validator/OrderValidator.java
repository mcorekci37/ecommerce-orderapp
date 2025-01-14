package com.emce.ecommerce.order.application.validator;

import com.emce.ecommerce.order.domain.entity.Order;

public abstract class OrderValidator {
    private OrderValidator nextValidator;

    public OrderValidator andThan(OrderValidator nextValidator) {
        this.nextValidator = nextValidator;
        return nextValidator;
    }

    public void validate(Order order) {
        if (nextValidator != null) {
            nextValidator.validate(order);
        }
    }
}
