package com.emce.ecommerce.order.domain.repository;

import com.emce.ecommerce.order.domain.entity.Order;

public interface OrderRepository {
    Order save(Order order);
}
