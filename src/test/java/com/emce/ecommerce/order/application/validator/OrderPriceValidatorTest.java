package com.emce.ecommerce.order.application.validator;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.config.OrderRestrictions;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.exception.OrderPriceExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OrderPriceValidatorTest {

    @Mock
    private Order order;

    @Mock
    private OrderRestrictions orderRestrictions;

    @InjectMocks
    private OrderPriceValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validate_throwsOrderPriceExceededException(){
        when(order.getTotalPrice()).thenReturn(Money.of(100));
        when(orderRestrictions.getMaximumAllowedPrice()).thenReturn(BigDecimal.valueOf(99));
        assertThrows(OrderPriceExceededException.class, () -> validator.validate(order));
    }

    @Test
    void validate_shouldValidateSuccessfully(){
        when(order.getTotalPrice()).thenReturn(Money.of(100));
        when(orderRestrictions.getMaximumAllowedPrice()).thenReturn(BigDecimal.valueOf(200));
        validator.validate(order);
    }

}
