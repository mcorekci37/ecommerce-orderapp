package com.emce.ecommerce.order.domain.exception;

public class ShippedOrderCannotBeCancelledException extends OrderDomainException {
  public ShippedOrderCannotBeCancelledException(String message) {
    super(message);
  }

  public ShippedOrderCannotBeCancelledException() {
    this("Order with id is already shipped. It cannot be cancelled.");
  }
}
