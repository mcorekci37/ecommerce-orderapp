package com.emce.ecommerce.order.application.validator;

import com.emce.ecommerce.order.domain.config.OrderRestrictions;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.exception.OrderQuantityExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderQuantityValidator extends OrderValidator {

  private final OrderRestrictions restrictions;

  @Override
  public void validate(Order order) {
    if (order.getQuantity() > restrictions.getMaximumAllowedQuantity()) {
      throw new OrderQuantityExceededException(
          order.getQuantity(), restrictions.getMaximumAllowedQuantity());
    }
    super.validate(order);
  }
}
