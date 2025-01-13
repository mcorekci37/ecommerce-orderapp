package com.emce.ecommerce.order.infrastructure.entity;

import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.product.infrastructure.entity.ProductEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class OrderEntity {
    @Id
    private String id;
    private Integer userId;
    @ManyToOne
    private ProductEntity product;
    private int quantity;
    private BigDecimal totalPrice;
    //todo implement auditing
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

}
