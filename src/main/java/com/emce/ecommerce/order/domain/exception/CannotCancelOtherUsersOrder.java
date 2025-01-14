package com.emce.ecommerce.order.domain.exception;

public class CannotCancelOtherUsersOrder extends OrderDomainException {
  public CannotCancelOtherUsersOrder(String message) {
    super(message);
  }

  public CannotCancelOtherUsersOrder() {
    this("This order belongs to other user. You cannot cancel it.");
  }
}
