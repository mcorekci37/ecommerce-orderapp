package com.emce.ecommerce.order.web.controller;

import com.emce.ecommerce.order.application.service.OrderService;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/createOrder")
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest){
    var response = orderService.create(orderRequest);
    return ResponseEntity.ok(response);
  }

}
