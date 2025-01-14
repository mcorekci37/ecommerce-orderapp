package com.emce.ecommerce.security.user.domain.repository;

import com.emce.ecommerce.security.user.domain.entity.Customer;
import com.emce.ecommerce.security.user.domain.valueobjects.CustomerId;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findById(CustomerId customerId);
    Customer save(Customer customer);
}
