package com.emce.ecommerce.order.web.dto;

import java.math.BigDecimal;

public record OrderRequest(
        Integer productId,
        Integer quantity,
        BigDecimal totalAmount
) {}
