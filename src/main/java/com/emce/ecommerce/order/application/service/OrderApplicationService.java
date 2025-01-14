package com.emce.ecommerce.order.application.service;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.order.application.mapper.OrderDataMapper;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.exception.CannotCancelOtherUsersOrderException;
import com.emce.ecommerce.order.domain.exception.OrderNotFoundException;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import com.emce.ecommerce.product.infrastructure.kafka.OrderProducer;
import com.emce.ecommerce.security.customer.domain.exception.CustomerDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static com.emce.ecommerce.common.domain.config.MessageConstants.MSG_ERR_COULD_NOT_SAVE_ORDER;
import static com.emce.ecommerce.common.domain.config.MessageConstants.MSG_ERR_COULD_NOT_SAVE_PRODUCT;
import static com.emce.ecommerce.security.auth.util.AuthUtil.getUsername;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderApplicationService {

  private final ProductRepository productRepository;
  private final OrderDataMapper mapper;
  private final OrderRepository orderRepository;
  private final OrderProducer producer;
  private final MessageConfig messageConfig;

  @CacheEvict(value = "orders", allEntries = true)
  public OrderResponse create(OrderRequest orderRequest) {
    //todo apply some validation logics such as quantity check etc.

    String username = getUsername();
    log.info("Order creation process started for product {} and user {}.", orderRequest.productId(), username);
    var productRequested = getProduct(orderRequest.productId());
    log.info("Product stock will be decreased with {}.", orderRequest.quantity());
    productRequested.consumeStock(orderRequest.quantity());
    log.info("Product stock is decreased with {}.", orderRequest.quantity());
    Order order = mapper.orderRequestToOrder(orderRequest, productRequested, username);

    Order savedOrder = saveOrder(order);
    producer.publishCreateEvent(savedOrder);

    //todo payment with webhooks

    log.info("Order successfully created for product {} and user {}.", orderRequest.productId(), username);
    return mapper.orderToOrderResponse(savedOrder);
  }

  private Order saveOrder(Order order) {
    Order result = orderRepository.save(order);
    if (result == null) {
      log.error("Could not save order for product {}!", order.getProduct().getId().getValue());
      throw new CustomerDomainException(messageConfig.getMessage(MSG_ERR_COULD_NOT_SAVE_ORDER));
    }
    return result;
  }

  private Product getProduct(Integer productId) {
    return productRepository
            .findByProductId(new ProductId(productId))
            .orElseThrow(() -> {
              log.error("Product not found with id {}.", productId);
              return new ProductNotFoundException(productId);
            });
  }

  @Cacheable(value = "orders", key = "#userId + ':' + #startDate + ':' + #endDate + ':' + #minAmount + ':' + #maxAmount + ':' + #pageable.pageNumber + ':' + #pageable.sort.toString()")
  public Page<OrderResponse> listOrders(LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
    String username = getUsername();
    log.info("Order listing process started for and user {}.", username);
    return orderRepository
        .findByUsernameAndDateBetweenAndTotalPriceBetween(
                username, startDate, endDate, minAmount, maxAmount, pageable)
           .map(mapper::orderToOrderResponse);
  }

  public OrderResponse cancelOrder(String orderId) {
    String loggedInUsername = getUsername();
    log.info("Order cancellation process started for and user {}.", loggedInUsername);
    Order order = getOrder(orderId);
    Product productInOrder = order.getProduct();

    validateCancellation(loggedInUsername, order);

    order.cancel();
    log.info("Order with id {} in cancelled and product stock is increased with {}.",
            order.getId().getValue(), order.getQuantity());

    Order savedOrder = saveAndPublishOrder(order, productInOrder);

    return mapper.orderToOrderResponse(savedOrder);
  }

  private Order saveAndPublishOrder(Order order, Product productInOrder) {
    Order savedOrder = saveOrder(order);
    producer.publishCancelEvent(savedOrder);

    saveProduct(productInOrder);
    return savedOrder;
  }

  private static void validateCancellation(String loggedInUsername, Order order) {
    //todo users having admin roles should do anything
    if (!loggedInUsername.equals(order.getUsername())){
      log.info("Order cancellation is stopped because logged in user is {} and order belongs to {} ",
              loggedInUsername, order.getUsername());
      throw new CannotCancelOtherUsersOrderException();
    }
  }

  private Order getOrder(String orderId) {
    return orderRepository
            .findByOrderId(new OrderId(orderId))
            .orElseThrow(() -> {
              log.error("Order not found with id {}", orderId);
              return new OrderNotFoundException(orderId);
            });
  }

  private Product saveProduct(Product product) {
    Product result = productRepository.save(product);
    if (result == null) {
      log.error("Could not save product {}!", product.getId().getValue());
      throw new CustomerDomainException(messageConfig.getMessage(MSG_ERR_COULD_NOT_SAVE_PRODUCT));
    }
    return result;
  }

}
