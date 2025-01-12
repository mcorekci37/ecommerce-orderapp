package com.emce.ecommerce.order.web.dto;

import com.emce.ecommerce.common.domain.valueobjects.Money;

import java.math.BigDecimal;

public record OrderRequest(
        Integer productId,
        Integer quantity,
        BigDecimal totalAmount
) {}
