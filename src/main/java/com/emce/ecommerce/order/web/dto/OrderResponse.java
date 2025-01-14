package com.emce.ecommerce.order.web.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String username,
        String orderId,
        Integer productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String orderStatus
) {}
