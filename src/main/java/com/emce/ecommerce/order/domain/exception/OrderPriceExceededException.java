package com.emce.ecommerce.order.domain.exception;

import java.math.BigDecimal;

public class OrderPriceExceededException extends OrderDomainException {
    private final BigDecimal requestedPrice;
    private final BigDecimal maxAllowedPrice;

    public OrderPriceExceededException(BigDecimal requestedPrice, BigDecimal maxAllowedPrice) {
        this.requestedPrice = requestedPrice;
        this.maxAllowedPrice = maxAllowedPrice;
    }

    public BigDecimal getRequestedPrice() {
        return requestedPrice;
    }

    public BigDecimal getMaxAllowedPrice() {
        return maxAllowedPrice;
    }
}
