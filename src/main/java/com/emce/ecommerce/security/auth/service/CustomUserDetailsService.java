package com.emce.ecommerce.security.auth.service;

import com.emce.ecommerce.security.user.domain.exception.UserNotFoundException;
import com.emce.ecommerce.security.user.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.user.domain.valueobjects.CustomerId;
import com.emce.ecommerce.security.user.infrastructure.entity.CustomerEntity;
import com.emce.ecommerce.security.user.infrastructure.mapper.CustomerEntityMapper;
import com.emce.ecommerce.security.user.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  public static final String USER_NOT_FOUND_MSG = "User not found with email %s";
  public static final String USER_ID_NOT_FOUND_MSG = "User not found with id %s";
  public static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists! mail: %s";

  private final CustomerRepository customerRepository;
  private final CustomerEntityMapper entityMapper;

  @Override
  public CustomerEntity loadUserByUsername(String username) throws UsernameNotFoundException {
    return customerRepository
        .findByEmail(username)
        .map(entityMapper::customerToCustomerEntity)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
  }

  public Customer loadUserById(Integer id) {
    return customerRepository
        .findById(new CustomerId(id))
        .orElseThrow(() -> new UserNotFoundException(String.format(USER_ID_NOT_FOUND_MSG, id)));
  }
}
