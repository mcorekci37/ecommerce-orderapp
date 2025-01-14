package com.emce.ecommerce.product.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductDomainException extends RuntimeException {

  public ProductDomainException(String message) {
    super(message);
  }
}
