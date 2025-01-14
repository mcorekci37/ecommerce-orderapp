package com.emce.ecommerce.security.auth.service;

import com.emce.ecommerce.security.user.domain.exception.CustomerNotFoundException;
import com.emce.ecommerce.security.user.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.user.infrastructure.entity.CustomerEntity;
import com.emce.ecommerce.security.user.infrastructure.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

  private final CustomerRepository customerRepository;
  private final CustomerEntityMapper entityMapper;

  @Override
  public CustomerEntity loadUserByUsername(String username) {
    return customerRepository
        .findByEmail(username)
        .map(entityMapper::customerToCustomerEntity)
        .orElseThrow(
            () -> {
              log.error("Customer not found with {}", username);
              return new CustomerNotFoundException(username);
            });
  }
}
