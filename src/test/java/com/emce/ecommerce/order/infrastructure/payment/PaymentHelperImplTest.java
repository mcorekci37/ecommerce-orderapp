package com.emce.ecommerce.order.infrastructure.payment;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentHelperImplTest {

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private PaymentHelperImpl paymentHelper;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create a mock order
        mockOrder = Mockito.mock(Order.class);
        when(mockOrder.getId()).thenReturn(new OrderId("ABB"));
    }

    @Test
    void processPayment_ShouldReturnTrue_WhenPaymentIsSuccessful() {
        // Arrange
        ResponseEntity<String> successResponse = new ResponseEntity<>("Payment Successful", HttpStatus.OK);
        when(paymentClient.processPayment(mockOrder)).thenReturn(successResponse);

        // Act
        boolean result = paymentHelper.processPayment(mockOrder);

        // Assert
        assertTrue(result);
        verify(paymentClient, times(1)).processPayment(mockOrder);
    }

    @Test
    void processPayment_ShouldReturnFalse_WhenPaymentFails() {
        // Arrange
        ResponseEntity<String> failureResponse = new ResponseEntity<>("Payment Failed", HttpStatus.BAD_REQUEST);
        when(paymentClient.processPayment(mockOrder)).thenReturn(failureResponse);

        // Act
        boolean result = paymentHelper.processPayment(mockOrder);

        // Assert
        assertFalse(result);
        verify(paymentClient, times(1)).processPayment(mockOrder);
    }

    @Test
    void processPayment_ShouldReturnFalse_WhenExceptionOccurs() {
        // Arrange
        when(paymentClient.processPayment(mockOrder)).thenThrow(new RuntimeException("Service Unavailable"));

        // Act
        boolean result = paymentHelper.processPayment(mockOrder);

        // Assert
        assertFalse(result);
        verify(paymentClient, times(1)).processPayment(mockOrder);
    }

    @Test
    void withdrawPayment_ShouldReturnTrue_WhenWithdrawIsSuccessful() {
        // Arrange
        ResponseEntity<String> successResponse = new ResponseEntity<>("Withdraw Successful", HttpStatus.OK);
        when(paymentClient.withdrawPayment(mockOrder)).thenReturn(successResponse);

        // Act
        boolean result = paymentHelper.withdrawPayment(mockOrder);

        // Assert
        assertTrue(result);
        verify(paymentClient, times(1)).withdrawPayment(mockOrder);
    }

    @Test
    void withdrawPayment_ShouldReturnFalse_WhenWithdrawFails() {
        // Arrange
        ResponseEntity<String> failureResponse = new ResponseEntity<>("Withdraw Failed", HttpStatus.BAD_REQUEST);
        when(paymentClient.withdrawPayment(mockOrder)).thenReturn(failureResponse);

        // Act
        boolean result = paymentHelper.withdrawPayment(mockOrder);

        // Assert
        assertFalse(result);
        verify(paymentClient, times(1)).withdrawPayment(mockOrder);
    }

    @Test
    void withdrawPayment_ShouldReturnFalse_WhenExceptionOccurs() {
        // Arrange
        when(paymentClient.withdrawPayment(mockOrder)).thenThrow(new RuntimeException("Service Unavailable"));

        // Act
        boolean result = paymentHelper.withdrawPayment(mockOrder);

        // Assert
        assertFalse(result);
        verify(paymentClient, times(1)).withdrawPayment(mockOrder);
    }
}
