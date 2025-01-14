package com.emce.ecommerce.order.application.validator;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.config.OrderRestrictions;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.exception.OrderPriceExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPriceValidator extends OrderValidator {

  private final OrderRestrictions restrictions;

  @Override
  public void validate(Order order) {
    if (order.getTotalPrice().isGreaterThan(new Money(restrictions.getMaximumAllowedPrice()))) {
      throw new OrderPriceExceededException(
          order.getTotalPrice().amount(), restrictions.getMaximumAllowedPrice());
    }
    super.validate(order);
  }
}
