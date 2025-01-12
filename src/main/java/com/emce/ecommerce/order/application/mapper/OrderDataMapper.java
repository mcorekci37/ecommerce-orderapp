package com.emce.ecommerce.order.application.mapper;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderDataMapper {

  public Order orderRequestToOrder(OrderRequest orderRequest, Product product) {
    return new Order(
            product,
            orderRequest.quantity(),
            new Money(orderRequest.totalAmount()));
  }

  public OrderResponse orderToOrderResponse(Order savedOrder) {
    return new OrderResponse(
            //todo user info will be added later after integration spring security
            1,
            "isim" + "soyisim",
            savedOrder.getId().getValue(),
            savedOrder.getProduct().getId().getValue(),
            savedOrder.getProduct().getName(),
            savedOrder.getProduct().getPrice().amount(),
            savedOrder.getQuantity(),
            savedOrder.getTotalPrice().amount(),
            savedOrder.getCreatedAt()
    );

  }
}
