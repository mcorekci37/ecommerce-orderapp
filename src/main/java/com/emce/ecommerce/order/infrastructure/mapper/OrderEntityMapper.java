package com.emce.ecommerce.order.infrastructure.mapper;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.infrastructure.entity.OrderEntity;
import com.emce.ecommerce.product.infrastructure.mapper.ProductEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEntityMapper {

  private final ProductEntityMapper productEntityMapper;

  public Order orderEntityToOrder(OrderEntity entity) {
    return new Order(
        new OrderId(entity.getId()),
        productEntityMapper.productEntityToProduct(entity.getProduct()),
        entity.getQuantity(),
        new Money(entity.getTotalPrice()),
        entity.getCreatedAt(),
        entity.getOrderStatus());
  }

  public OrderEntity orderToOrderEntity(Order order) {
    return new OrderEntity(
        order.getId().getValue(),
        productEntityMapper.productToProductEntity(order.getProduct()),
        order.getQuantity(),
        order.getTotalPrice().amount(),
        order.getCreatedAt(),
        order.getOrderStatus());
  }
}
