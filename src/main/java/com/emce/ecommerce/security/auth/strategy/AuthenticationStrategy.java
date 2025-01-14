package com.emce.ecommerce.security.auth.strategy;

import com.emce.ecommerce.security.customer.web.dto.AuthRequest;

public interface AuthenticationStrategy {
  void authenticate(AuthRequest request);
}
