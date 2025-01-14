package com.emce.ecommerce.product.exception;

public class OutOfStockException extends ProductDomainException {
  private final long quantity;

  public OutOfStockException(long quantity) {
    this.quantity = quantity;
  }

  public long getQuantity() {
    return quantity;
  }
}
