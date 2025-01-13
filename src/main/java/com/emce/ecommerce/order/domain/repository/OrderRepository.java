package com.emce.ecommerce.order.domain.repository;

import com.emce.ecommerce.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderRepository {
    Order save(Order order);
    Page<Order> findByUserIdAndDateBetweenAndTotalPriceBetween(Integer userId,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate,
                                                               BigDecimal minAmount,
                                                               BigDecimal maxAmount,
                                                               Pageable pageable);
}
