package com.emce.ecommerce.security.user.infrastructure.mapper;

import com.emce.ecommerce.security.user.domain.entity.Customer;
import com.emce.ecommerce.security.user.domain.valueobjects.CustomerId;
import com.emce.ecommerce.security.user.infrastructure.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityMapper {
  public Customer customerEntityToCustomer(CustomerEntity entity) {
    return new Customer(
        new CustomerId(entity.getId()),
        entity.getName(),
        entity.getEmail(),
        entity.getPassword(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getRole());
  }

  public CustomerEntity customerToCustomerEntity(Customer customer) {
    return new CustomerEntity(
        customer.getName(),
        customer.getEmail(),
        customer.getPassword(),
        customer.getRole());
  }
}
