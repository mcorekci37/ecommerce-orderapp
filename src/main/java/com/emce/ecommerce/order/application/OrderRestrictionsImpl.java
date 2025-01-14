package com.emce.ecommerce.order.application;

import com.emce.ecommerce.order.domain.config.OrderRestrictions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RefreshScope
public class OrderRestrictionsImpl implements OrderRestrictions {

  @Value("${order.restrictions.max-allowed-quantity:5}")
  private int maxAllowedQuantity;

  @Value("${order.restrictions.max-allowed-price:200000}")
  private BigDecimal maxAllowedPrice;

  @Override
  public int getMaximumAllowedQuantity() {
    return maxAllowedQuantity;
  }

  @Override
  public BigDecimal getMaximumAllowedPrice() {
    return maxAllowedPrice;
  }
}
