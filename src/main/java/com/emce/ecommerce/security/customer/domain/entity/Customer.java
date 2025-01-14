package com.emce.ecommerce.security.customer.domain.entity;

import com.emce.ecommerce.common.domain.entity.AggregateRoot;
import com.emce.ecommerce.security.customer.domain.valueobjects.CustomerId;
import com.emce.ecommerce.security.customer.domain.valueobjects.Role;

import java.time.LocalDateTime;

public class Customer extends AggregateRoot<CustomerId> {

  private String name;
  private String email;
  private String password;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Role role;

  public Customer(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = Role.USER;
  }

  public Customer(
      CustomerId customerId,
      String name,
      String email,
      String password,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Role role) {
    setId(customerId);
    this.name = name;
    this.email = email;
    this.password = password;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.role = role;
  }

  public Customer(CustomerId customerId) {
    setId(customerId);
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public Role getRole() {
    return role;
  }
}
