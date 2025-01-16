package com.emce.ecommerce.order.application.validator;

import com.emce.ecommerce.order.domain.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OrderValidatorTest {

    private OrderValidator firstValidator;
    private OrderValidator secondValidator;
    private Order order;

    @BeforeEach
    void setUp() {
        firstValidator = Mockito.spy(new TestOrderValidator());
        secondValidator = Mockito.spy(new TestOrderValidator());

        order = mock(Order.class);
    }

    @Test
    void andThan_ShouldChainValidatorsCorrectly() {
        OrderValidator chainedValidator = firstValidator.andThan(secondValidator);

        assertNotNull(chainedValidator);
        verifyNoInteractions(order);
    }

    @Test
    void validate_ShouldCallNextValidatorInChain() {
        firstValidator.andThan(secondValidator);

        firstValidator.validate(order);

        verify(firstValidator, times(1)).validate(order);
        verify(secondValidator, times(1)).validate(order);
    }

    @Test
    void validate_ShouldNotCallNextValidatorIfNotChained() {
        firstValidator.validate(order);

        verify(firstValidator, times(1)).validate(order);
        verify(secondValidator, never()).validate(order);
    }

    private static class TestOrderValidator extends OrderValidator {
        @Override
        public void validate(Order order) {
            super.validate(order);
        }
    }
}
