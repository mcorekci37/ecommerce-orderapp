package com.emce.ecommerce.order.domain.exception;

public class ShippedOrderCannotBeCancelledException extends OrderDomainException {

  private final String orderId;


  public ShippedOrderCannotBeCancelledException(String orderId) {
    this.orderId = orderId;
  }

  public String getOrderId() {
    return orderId;
  }
}
