package com.emce.ecommerce.security.token;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.security.auth.util.JwtUtil;
import com.emce.ecommerce.security.customer.domain.exception.CustomerNotFoundException;
import com.emce.ecommerce.security.customer.domain.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import static com.emce.ecommerce.common.domain.config.MessageConstants.MSG_TOKEN_EXPIRED;

@Component
@Slf4j
public class CustomerTokenValidator extends TokenValidator {
  private final CustomerRepository customerRepository;

  public CustomerTokenValidator(
      JwtUtil jwtUtil, CustomerRepository customerRepository, MessageConfig messageConfig) {
    super(jwtUtil, messageConfig);
    this.customerRepository = customerRepository;
  }

  @Override
  protected void validateToken(String token, String userEmail) {
    if (!jwtUtil.isTokenValid(token, userEmail)) {
      log.info("Token is not valid.");
      throw new CredentialsExpiredException(messageConfig.getMessage(MSG_TOKEN_EXPIRED));
    }
  }

  @Override
  protected Integer getUserId(String userEmail) {
    return customerRepository
        .findByEmail(userEmail)
        .orElseThrow(
            () -> {
              log.info("Customer not found with {}", userEmail);
              return new CustomerNotFoundException(userEmail);
            })
        .getId()
        .getValue();
  }
}
