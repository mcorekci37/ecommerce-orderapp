package com.emce.ecommerce.order.domain.exception;

import com.emce.ecommerce.product.exception.ProductDomainException;

public class OrderNotFoundException extends OrderDomainException {
    public OrderNotFoundException(String orderId) {
        super(String.format("Order with id %s not found", orderId));
    }

}
