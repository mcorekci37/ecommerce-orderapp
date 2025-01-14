package com.emce.ecommerce.security.auth.strategy;

import com.emce.ecommerce.security.customer.web.dto.AuthRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationContext {
  private AuthenticationStrategy strategy;

  public void setStrategy(AuthenticationStrategy strategy) {
    this.strategy = strategy;
  }

  public void authenticate(AuthRequest request) {
    strategy.authenticate(request);
  }
}
