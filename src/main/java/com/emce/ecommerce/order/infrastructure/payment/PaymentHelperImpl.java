package com.emce.ecommerce.order.infrastructure.payment;

import com.emce.ecommerce.order.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentHelperImpl implements PaymentHelper {

  private final PaymentClient paymentClient;

  public boolean processPayment(Order order) {
    try {
      // Mocking payment processing via webhook
      log.info(
          "Processing payment for order {} with total price {}.",
          order.getId().getValue(),
          order.getTotalPrice());

      // Simulate an HTTP call to a payment provider
      ResponseEntity<String> response = paymentClient.processPayment(order);
      if (response.getStatusCode().is2xxSuccessful()) {
        log.info("Payment processed successfully for order {}.", order.getId().getValue());
        return true;
      } else {
        log.error(
            "Payment failed with status {} for order {}.",
            response.getStatusCode(),
            order.getId().getValue());
        return false;
      }
    } catch (Exception e) {
      log.error(
          "Exception occurred while processing payment for order {}: {}",
          order.getId().getValue(),
          e.getMessage());
      return false;
    }
  }

  @Override
  public boolean withdrawPayment(Order order) {
    try {
      log.info(
          "Withdrawing payment for order {} with total price {}.",
          order.getId().getValue(),
          order.getTotalPrice());

      ResponseEntity<String> response = paymentClient.withdrawPayment(order);
      if (response.getStatusCode().is2xxSuccessful()) {
        log.info("Payment withdraw successfully for order {}.", order.getId().getValue());
        return true;
      } else {
        log.error(
            "Payment withdrawing failed with status {} for order {}.",
            response.getStatusCode(),
            order.getId().getValue());
        return false;
      }
    } catch (Exception e) {
      log.error(
          "Exception occurred while processing payment withdraw for order {}: {}",
          order.getId().getValue(),
          e.getMessage());
      return false;
    }
  }
}
