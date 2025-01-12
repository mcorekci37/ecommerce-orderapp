package com.emce.ecommerce.order.infrastructure.adapter;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.infrastructure.mapper.OrderEntityMapper;
import com.emce.ecommerce.order.infrastructure.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository jpaRepository;
  private final OrderEntityMapper mapper;

  @Override
  public Order save(Order order) {
    return mapper.orderEntityToOrder(jpaRepository.save(mapper.orderToOrderEntity(order)));
  }
}
