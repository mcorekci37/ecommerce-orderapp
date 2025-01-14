package com.emce.ecommerce.order.domain.config;

import java.math.BigDecimal;

public interface OrderRestrictions {
    int getMaximumAllowedQuantity();
    BigDecimal getMaximumAllowedPrice();
}
