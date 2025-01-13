package com.emce.ecommerce.order.web.dto;

import com.emce.ecommerce.common.domain.valueobjects.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Integer userId,
        String userDetails,
        String orderId,
        Integer productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String orderStatus
) {}
