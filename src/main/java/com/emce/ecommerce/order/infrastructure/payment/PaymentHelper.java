package com.emce.ecommerce.order.infrastructure.payment;

import com.emce.ecommerce.order.domain.entity.Order;

public interface PaymentHelper {
    boolean processPayment(Order order);
    boolean withdrawPayment(Order order);
}

