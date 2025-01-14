package com.emce.ecommerce.security.user.infrastructure.repository;

import java.util.Optional;

import com.emce.ecommerce.security.user.domain.entity.Customer;
import com.emce.ecommerce.security.user.infrastructure.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Integer> {
    Optional<CustomerEntity> findByEmail(String email);
}
