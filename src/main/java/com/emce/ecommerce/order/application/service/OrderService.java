package com.emce.ecommerce.order.application.service;

import com.emce.ecommerce.order.application.mapper.OrderDataMapper;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import com.emce.ecommerce.product.infrastructure.kafka.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final ProductRepository productRepository;
  private final OrderDataMapper mapper;
  private final OrderRepository repository;
  private final OrderProducer producer;

  public OrderResponse create(OrderRequest orderRequest) {
    var productRequested =
        productRepository
            .findByProductId(new ProductId(orderRequest.productId()))
            .orElseThrow(() -> new ProductNotFoundException(orderRequest.productId()));
    productRequested.consumeStock(orderRequest.quantity());
    Order order = mapper.orderRequestToOrder(orderRequest, productRequested);

    Order savedOrder = repository.save(order);
    producer.publishOrder(savedOrder);

    //todo payment with webhooks

    return mapper.orderToOrderResponse(savedOrder);
  }
}
