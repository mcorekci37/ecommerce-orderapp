package com.emce.ecommerce.order.infrastructure.exception;

import com.emce.ecommerce.order.domain.exception.OrderDomainException;
import com.emce.ecommerce.product.exception.ProductDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<Object> handleOrderDomainException(OrderDomainException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ProductDomainException.class)
    public ResponseEntity<Object> handleProductDomainException(ProductDomainException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
