package com.emce.ecommerce.security.auth.strategy;

import com.emce.ecommerce.security.customer.web.dto.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationStrategy implements AuthenticationStrategy {
  private final AuthenticationManager authenticationManager;

  @Override
  public void authenticate(AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));
  }
}
