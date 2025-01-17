package com.emce.ecommerce.order.infrastructure.adapter;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.infrastructure.mapper.OrderEntityMapper;
import com.emce.ecommerce.order.infrastructure.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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
  public Optional<Order> findByOrderId(OrderId orderId) {
    return jpaRepository.findById(orderId.getValue()).map(mapper::orderEntityToOrder);
  }

  @Override
  public Page<Order> findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(String username, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
    return jpaRepository.findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(username, startDate, endDate, minAmount, maxAmount, pageable)
            .map(mapper::orderEntityToOrder);
  }
}
