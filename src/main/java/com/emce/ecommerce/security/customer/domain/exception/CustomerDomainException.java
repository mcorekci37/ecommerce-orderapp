package com.emce.ecommerce.security.customer.domain.exception;

public class CustomerDomainException extends RuntimeException {
  public CustomerDomainException(String message) {
    super(message);
  }

  public CustomerDomainException() {
  }
}
