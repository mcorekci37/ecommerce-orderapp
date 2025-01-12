package com.emce.ecommerce.order.infrastructure.repository;

import com.emce.ecommerce.order.infrastructure.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {}
