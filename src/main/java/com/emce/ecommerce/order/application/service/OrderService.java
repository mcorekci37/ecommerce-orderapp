package com.emce.ecommerce.order.application.service;

import com.emce.ecommerce.order.application.mapper.OrderDataMapper;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.exception.CannotCancelOtherUsersOrder;
import com.emce.ecommerce.order.domain.exception.OrderNotFoundException;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import com.emce.ecommerce.product.infrastructure.kafka.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static com.emce.ecommerce.security.auth.util.AuthUtil.getUsername;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

  private final ProductRepository productRepository;
  private final OrderDataMapper mapper;
  private final OrderRepository orderRepository;
  private final OrderProducer producer;

  @CacheEvict(value = "orders", allEntries = true)
  public OrderResponse create(OrderRequest orderRequest) {
    //todo apply some validation logics such as quantity check etc.

    var productRequested =
        productRepository
            .findByProductId(new ProductId(orderRequest.productId()))
            .orElseThrow(() -> new ProductNotFoundException(orderRequest.productId()));
    productRequested.consumeStock(orderRequest.quantity());
    Order order = mapper.orderRequestToOrder(orderRequest, productRequested, getUsername());

    Order savedOrder = orderRepository.save(order);
    producer.publishCreateEvent(savedOrder);

    //todo payment with webhooks

    return mapper.orderToOrderResponse(savedOrder);
  }

  @Cacheable(value = "orders", key = "#userId + ':' + #startDate + ':' + #endDate + ':' + #minAmount + ':' + #maxAmount + ':' + #pageable.pageNumber + ':' + #pageable.sort.toString()")
  public Page<OrderResponse> listOrders(LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
    return orderRepository
        .findByUsernameAndDateBetweenAndTotalPriceBetween(
            getUsername(), startDate, endDate, minAmount, maxAmount, pageable)
           .map(mapper::orderToOrderResponse);
  }

  public OrderResponse cancelOrder(String orderId) {
    Order order = orderRepository
            .findByOrderId(new OrderId(orderId))
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    //todo users having admin roles should do anything
    if (!getUsername().equals(order.getUsername())){
      throw new CannotCancelOtherUsersOrder();
    }
    order.cancel();
    Order savedOrder = orderRepository.save(order);
    producer.publishCancelEvent(savedOrder);

    return mapper.orderToOrderResponse(savedOrder);
  }
}
