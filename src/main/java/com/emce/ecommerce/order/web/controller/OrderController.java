package com.emce.ecommerce.order.web.controller;

import com.emce.ecommerce.order.application.service.OrderApplicationService;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

  private final OrderApplicationService orderApplicationService;

  @PostMapping("/createOrder")
  @Operation(summary = "Create a new order", description = "Creates a new order for a product for a logged in user")
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
    log.info("Order creation request came for product {}", orderRequest.productId());
    var response = orderApplicationService.create(orderRequest);
    log.info("Order creation request completed for product {}", orderRequest.productId());
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "List orders for applied filters", description = "List all orders of logged in user with filters and pagination")
  public ResponseEntity<Page<OrderResponse>> listOrders(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(defaultValue = "0") BigDecimal minAmount,
      @RequestParam(defaultValue = "1000000") BigDecimal maxAmount,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id,asc") String[] sort
  ) {
    log.info("Order listing request came.");
    if (startDate == null) {
      startDate = LocalDateTime.now().minusDays(7);
    }
    if (endDate == null) {
      endDate = LocalDateTime.now();
    }

    Pageable pageable = getPageable(page, size, sort);

    Page<OrderResponse> orders =
        orderApplicationService.listOrders(startDate, endDate, minAmount, maxAmount, pageable);
    log.info("Order listing request completed.");
    return ResponseEntity.ok(orders);
  }

  @DeleteMapping("/cancel/{orderNumber}")
  @Operation(summary = "Cancels an order", description = "Cancels an order for a logged in user if not shipped")
  public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderNumber) {
    log.info("Order cancellation request came for order {}.", orderNumber);
    OrderResponse response = orderApplicationService.cancelOrder(orderNumber);
    log.info("Order cancellation request completed for order {}.", orderNumber);
    return ResponseEntity.ok(response);
  }

  private static Pageable getPageable(int page, int size, String[] sort) {
    Sort.Direction direction = Sort.Direction.fromString(sort[1]);
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
    return pageable;
  }

}
