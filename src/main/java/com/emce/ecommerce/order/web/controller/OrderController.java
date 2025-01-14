package com.emce.ecommerce.order.web.controller;

import com.emce.ecommerce.order.application.service.OrderApplicationService;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {

  private final OrderApplicationService orderApplicationService;

  @PostMapping("/createOrder")
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
    log.info("Order creation request came for product {}", orderRequest.productId());
    var response = orderApplicationService.create(orderRequest);
    log.info("Order creation request completed for product {}", orderRequest.productId());
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<OrderResponse>> listOrders(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(defaultValue = "0") BigDecimal minAmount,
      @RequestParam(defaultValue = "1000000") BigDecimal maxAmount,
      Pageable pageable
      ) {
    log.info("Order listing request came.");
    Page<OrderResponse> orders =
        orderApplicationService.listOrders(startDate, endDate, minAmount, maxAmount, pageable);
    log.info("Order listing request completed.");
    return ResponseEntity.ok(orders);
  }

  @DeleteMapping("/cancel/{orderNumber}")
  public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderNumber) {
    log.info("Order cancellation request came for order {}.", orderNumber);
    OrderResponse response = orderApplicationService.cancelOrder(orderNumber);
    log.info("Order cancellation request completed for order {}.", orderNumber);
    return ResponseEntity.ok(response);
  }
}
