package com.emce.ecommerce.order.domain.exception;

public class OrderNotFoundException extends OrderDomainException {
    private final String orderId;

    public OrderNotFoundException(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
