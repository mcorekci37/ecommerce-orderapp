package com.emce.ecommerce.order.application.mapper;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderDataMapper {

  public Order orderRequestToOrder(OrderRequest orderRequest, Product product, String username) {
    return new Order(
            username,
            product,
            orderRequest.quantity(),
            new Money(orderRequest.totalAmount()));
  }

  public OrderResponse orderToOrderResponse(Order savedOrder) {
    return new OrderResponse(
            savedOrder.getUsername(),
            savedOrder.getId().getValue(),
            savedOrder.getProduct().getId().getValue(),
            savedOrder.getProduct().getName(),
            savedOrder.getProduct().getPrice().amount(),
            savedOrder.getQuantity(),
            savedOrder.getTotalPrice().amount(),
            savedOrder.getCreatedAt(),
            savedOrder.getUpdatedAt(),
            savedOrder.getOrderStatus().toString()
    );

  }
}
