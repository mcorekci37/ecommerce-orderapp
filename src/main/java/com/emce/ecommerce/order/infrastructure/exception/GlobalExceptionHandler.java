package com.emce.ecommerce.order.infrastructure.exception;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.order.domain.exception.CannotCancelOtherUsersOrderException;
import com.emce.ecommerce.order.domain.exception.OrderDomainException;
import com.emce.ecommerce.order.domain.exception.OrderNotFoundException;
import com.emce.ecommerce.order.domain.exception.ShippedOrderCannotBeCancelledException;
import com.emce.ecommerce.product.exception.OutOfStockException;
import com.emce.ecommerce.product.exception.ProductDomainException;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import com.emce.ecommerce.security.user.domain.exception.CustomerDomainException;
import com.emce.ecommerce.security.user.domain.exception.DuplicateEmailException;
import com.emce.ecommerce.security.user.domain.exception.CustomerNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import static com.emce.ecommerce.common.domain.config.MessageConstants.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageConfig messageConfig;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Object> handleOutOfStockException(OutOfStockException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_OUT_OF_STOCK, ex.getQuantity()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_PRODUCT_NOT_FOUND, ex.getProductId()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(CustomerNotFoundException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_USERNAME_NOT_FOUND, ex.getUsername()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Object> handleDuplicateEmailException(DuplicateEmailException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_EMAIL_ALREADY_EXISTS, ex.getEmail()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CannotCancelOtherUsersOrderException.class)
    public ResponseEntity<Object> handleCannotCancelOtherUsersOrderException(CannotCancelOtherUsersOrderException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_CANNOT_CANCEL_OTHER_USERS_ORDER), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFoundException(OrderNotFoundException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_ORDER_NOT_FOUND, ex.getOrderId()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShippedOrderCannotBeCancelledException.class)
    public ResponseEntity<Object> handleShippedOrderCannotBeCancelledException(ShippedOrderCannotBeCancelledException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_SHIPPED_ORDER_CANNOT_BE_CANCELLED, ex.getOrderId()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(messageConfig.getMessage(MSG_AUTHENTICATION_FAILED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<Object> handleOrderDomainException(OrderDomainException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductDomainException.class)
    public ResponseEntity<Object> handleProductDomainException(ProductDomainException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomerDomainException.class)
    public ResponseEntity<Object> handleCustomerDomainException(CustomerDomainException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
