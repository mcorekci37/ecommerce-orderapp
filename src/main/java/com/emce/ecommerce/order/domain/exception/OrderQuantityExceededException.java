package com.emce.ecommerce.order.domain.exception;

public class OrderQuantityExceededException extends OrderDomainException {
    private final int requestedQuantity;
    private final int maxAllowedQuantity;

    public OrderQuantityExceededException(int requestedQuantity, int maxAllowedQuantity) {
        this.requestedQuantity = requestedQuantity;
        this.maxAllowedQuantity = maxAllowedQuantity;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getMaxAllowedQuantity() {
        return maxAllowedQuantity;
    }
}
