package com.emce.ecommerce.order.web.exception;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.order.domain.exception.*;
import com.emce.ecommerce.product.exception.OutOfStockException;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.emce.ecommerce.common.domain.config.MessageConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Mock
    private MessageConfig messageConfig;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequest() {
        RuntimeException exception = new RuntimeException("Test runtime exception");

        ResponseEntity<Object> response = exceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test runtime exception", response.getBody());
    }

    @Test
    void handleOutOfStockException_ShouldReturnBadRequest() {
        OutOfStockException exception = new OutOfStockException(5);
        when(messageConfig.getMessage(MSG_OUT_OF_STOCK, exception.getQuantity()))
                .thenReturn("Out of stock for quantity: 5");

        ResponseEntity<Object> response = exceptionHandler.handleOutOfStockException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Out of stock for quantity: 5", response.getBody());
    }

    @Test
    void handleProductNotFoundException_ShouldReturnBadRequest() {
        ProductNotFoundException exception = new ProductNotFoundException(101);
        when(messageConfig.getMessage(MSG_PRODUCT_NOT_FOUND, exception.getProductId()))
                .thenReturn("Product not found with ID: 101");

        ResponseEntity<Object> response = exceptionHandler.handleProductNotFoundException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product not found with ID: 101", response.getBody());
    }

    @Test
    void handleCannotCancelOtherUsersOrderException_ShouldReturnBadRequest() {
        CannotCancelOtherUsersOrderException exception = new CannotCancelOtherUsersOrderException();
        when(messageConfig.getMessage(MSG_CANNOT_CANCEL_OTHER_USERS_ORDER))
                .thenReturn("Cannot cancel another user's order");

        ResponseEntity<Object> response = exceptionHandler.handleCannotCancelOtherUsersOrderException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot cancel another user's order", response.getBody());
    }

    @Test
    void handleOrderNotFoundException_ShouldReturnBadRequest() {
        OrderNotFoundException exception = new OrderNotFoundException("123L");
        when(messageConfig.getMessage(MSG_ORDER_NOT_FOUND, exception.getOrderId()))
                .thenReturn("Order not found with ID: 123");

        ResponseEntity<Object> response = exceptionHandler.handleOrderNotFoundException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order not found with ID: 123", response.getBody());
    }

    @Test
    void handleShippedOrderCannotBeCancelledException_ShouldReturnBadRequest() {
        ShippedOrderCannotBeCancelledException exception = new ShippedOrderCannotBeCancelledException("456L");
        when(messageConfig.getMessage(MSG_SHIPPED_ORDER_CANNOT_BE_CANCELLED, exception.getOrderId()))
                .thenReturn("Shipped order with ID 456 cannot be cancelled");

        ResponseEntity<Object> response = exceptionHandler.handleShippedOrderCannotBeCancelledException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Shipped order with ID 456 cannot be cancelled", response.getBody());
    }

    @Test
    void handleOrderQuantityExceededException_ShouldReturnBadRequest() {
        OrderQuantityExceededException exception = new OrderQuantityExceededException(10, 5);
        when(messageConfig.getMessage(MSG_ORDER_QUANTITY_EXCEEDED, exception.getRequestedQuantity(), exception.getMaxAllowedQuantity()))
                .thenReturn("Requested quantity 10 exceeds max allowed 5");

        ResponseEntity<Object> response = exceptionHandler.handleOrderQuantityExceededException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requested quantity 10 exceeds max allowed 5", response.getBody());
    }

    @Test
    void handleOrderPriceExceededException_ShouldReturnBadRequest() {
        OrderPriceExceededException exception = new OrderPriceExceededException(BigDecimal.valueOf(200), BigDecimal.valueOf(100));
        when(messageConfig.getMessage(MSG_ORDER_PRICE_EXCEEDED, exception.getRequestedPrice(), exception.getMaxAllowedPrice()))
                .thenReturn("Requested price 200 exceeds max allowed 100");

        ResponseEntity<Object> response = exceptionHandler.handleOrderPriceExceededException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requested price 200 exceeds max allowed 100", response.getBody());
    }

    @Test
    void handleConstraintViolation_ShouldReturnValidationErrors() {
        ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(PathImpl.createPathFromString("fieldName"));
        when(violation.getMessage()).thenReturn("must not be blank");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleConstraintViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("fieldName"));
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnValidationErrors() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be blank");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("fieldName"));
    }
}
