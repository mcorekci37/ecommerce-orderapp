package com.emce.ecommerce.order.application.service;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.order.application.mapper.OrderDataMapper;
import com.emce.ecommerce.order.application.validator.OrderPriceValidator;
import com.emce.ecommerce.order.application.validator.OrderQuantityValidator;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.events.EventType;
import com.emce.ecommerce.order.domain.events.OrderEvent;
import com.emce.ecommerce.order.domain.exception.CannotCancelOtherUsersOrderException;
import com.emce.ecommerce.order.domain.exception.OrderNotFoundException;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.infrastructure.kafka.EventPublisher;
import com.emce.ecommerce.order.infrastructure.payment.PaymentHelper;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import com.emce.ecommerce.security.customer.domain.exception.CustomerDomainException;
import com.emce.ecommerce.security.customer.domain.valueobjects.Role;
import jakarta.transaction.Transactional;
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
import static com.emce.ecommerce.security.auth.util.AuthUtil.getRole;
import static com.emce.ecommerce.security.auth.util.AuthUtil.getUsername;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderApplicationService {

  private final ProductRepository productRepository;
  private final OrderDataMapper mapper;
  private final OrderRepository orderRepository;
  private final EventPublisher eventPublisher;
  private final MessageConfig messageConfig;
  private final OrderQuantityValidator orderQuantityValidator;
  private final OrderPriceValidator orderPriceValidator;
  private final PaymentHelper paymentHelper;

  @CacheEvict(value = "orders", allEntries = true)
  @Transactional
  public OrderResponse create(OrderRequest orderRequest) {
    String username = getUsername();
    log.info("Order creation process started for product {} and user {}.", orderRequest.productId(), username);
    Order order = validateAndCreateOrder(orderRequest, username);

    Order savedOrder = saveAndPublishOrder(new OrderEvent(order, EventType.CREATE));

    paymentHelper.processPayment(savedOrder);

    log.info("Order successfully created for product {} and user {}.", orderRequest.productId(), username);
    return mapper.orderToOrderResponse(savedOrder);
  }

  @Cacheable(value = "orders",
      key = "#startDate + ':' + #endDate + ':' + #minAmount + ':' + #maxAmount + ':' + #pageable.pageNumber + ':' + #pageable.sort.toString()")
  public Page<OrderResponse> listOrders(LocalDateTime startDate, LocalDateTime endDate,
      BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
    String username = getUsername();
    log.info("Order listing process started for and user {}.", username);
    return orderRepository
        .findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(
            username, startDate, endDate, minAmount, maxAmount, pageable)
        .map(mapper::orderToOrderResponse);
  }

  @Transactional
  @CacheEvict(value = "orders", allEntries = true)
  public OrderResponse cancelOrder(String orderId) {
    String loggedInUsername = getUsername();
    log.info("Order cancellation process started for and user {}.", loggedInUsername);
    Order order = getOrder(orderId);

    validateCancellation(loggedInUsername, order);

    order.cancel();
    log.info(
        "Order with id {} in cancelled and product stock is increased with {}.",
        order.getId().getValue(),
        order.getQuantity());

    Order savedOrder = saveAndPublishOrder(new OrderEvent(order, EventType.CANCEL));

    paymentHelper.withdrawPayment(savedOrder);

    return mapper.orderToOrderResponse(savedOrder);
  }

  private Order validateAndCreateOrder(OrderRequest orderRequest, String username) {
    log.info("Product stock will be decreased with {}.", orderRequest.quantity());
    var productRequested = getProduct(orderRequest.productId());
    Order order = mapper.orderRequestToOrder(orderRequest, productRequested, username);
    log.info("Product stock is decreased with {}.", orderRequest.quantity());

    validateOrder(order);

    return order;
  }

  private void validateOrder(Order order) {
    orderQuantityValidator
            .andThan(orderPriceValidator);
    orderQuantityValidator.validate(order);
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
            .orElseThrow(
                    () -> {
                      log.error("Product not found with id {}.", productId);
                      return new ProductNotFoundException(productId);
                    });
  }


  private Order saveAndPublishOrder(OrderEvent event) {
    Order savedOrder = saveOrder(event.getOrder());
    eventPublisher.publish(event);

    saveProduct(event.getOrder().getProduct());
    return savedOrder;
  }

  private static void validateCancellation(String loggedInUsername, Order order) {
    if (!loggedInUsername.equals(order.getUsername()) && getRole()!= Role.ADMIN) {
      log.info(
          "Order cancellation is stopped because logged in user is {} and order belongs to {} ",
          loggedInUsername,
          order.getUsername());
      throw new CannotCancelOtherUsersOrderException();
    }
  }

  private Order getOrder(String orderId) {
    return orderRepository
        .findByOrderId(new OrderId(orderId))
        .orElseThrow(
            () -> {
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
