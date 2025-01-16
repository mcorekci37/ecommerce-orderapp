package com.emce.ecommerce.order.application.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.emce.ecommerce.order.domain.config.OrderRestrictions;
import com.emce.ecommerce.order.domain.entity.Order;

import com.emce.ecommerce.order.domain.exception.OrderQuantityExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderQuantityValidatorTest {

    @Mock
    private Order order;

    @Mock
    private OrderRestrictions orderRestrictions;

    @InjectMocks
    private OrderQuantityValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validate_throwsOrderQuantityExceededException(){
        when(order.getQuantity()).thenReturn(10);
        when(orderRestrictions.getMaximumAllowedQuantity()).thenReturn(5);
        assertThrows(OrderQuantityExceededException.class, () -> validator.validate(order));
    }

    @Test
    void validate_shouldValidateSuccessfully(){
        when(order.getQuantity()).thenReturn(10);
        when(orderRestrictions.getMaximumAllowedQuantity()).thenReturn(20);
        validator.validate(order);
    }

}
