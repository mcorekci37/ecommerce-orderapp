package com.emce.ecommerce.order.infrastructure.repository;

import com.emce.ecommerce.order.infrastructure.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    Page<OrderEntity> findByUsernameAndDateBetweenAndTotalPriceBetween(
            String username,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);
}
