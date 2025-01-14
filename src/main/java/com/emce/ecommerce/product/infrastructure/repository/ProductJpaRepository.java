package com.emce.ecommerce.product.infrastructure.repository;

import com.emce.ecommerce.product.infrastructure.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {}
