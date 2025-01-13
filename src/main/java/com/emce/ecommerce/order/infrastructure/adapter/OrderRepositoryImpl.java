package com.emce.ecommerce.order.infrastructure.adapter;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.infrastructure.mapper.OrderEntityMapper;
import com.emce.ecommerce.order.infrastructure.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository jpaRepository;
  private final OrderEntityMapper mapper;

  @Override
  public Order save(Order order) {
    return mapper.orderEntityToOrder(jpaRepository.save(mapper.orderToOrderEntity(order)));
  }

  @Override
  public Page<Order> findByUserIdAndDateBetweenAndTotalPriceBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
    return jpaRepository.findByUserIdAndDateBetweenAndTotalPriceBetween(userId, startDate, endDate, minAmount, maxAmount, pageable)
            .map(mapper::orderEntityToOrder);
  }
}
