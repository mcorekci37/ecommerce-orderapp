package com.emce.ecommerce.order.domain.repository;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findByOrderId(OrderId orderId);
    Page<Order> findByUserIdAndDateBetweenAndTotalPriceBetween(Integer userId,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate,
                                                               BigDecimal minAmount,
                                                               BigDecimal maxAmount,
                                                               Pageable pageable);
}
