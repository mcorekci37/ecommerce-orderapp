package com.emce.ecommerce.security.token;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.security.auth.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;

import static com.emce.ecommerce.common.domain.config.MessageConstants.MSG_TOKEN_NOT_VALID;

public abstract class TokenValidator {
  protected final JwtUtil jwtUtil;
  protected final MessageConfig messageConfig;

  protected TokenValidator(JwtUtil jwtUtil, MessageConfig messageConfig) {
    this.jwtUtil = jwtUtil;
    this.messageConfig = messageConfig;
  }

  public final Integer validate(String token) {
    String userEmail = extractUserEmail(token);
    validateToken(token, userEmail);
    return getUserId(userEmail);
  }

  protected String extractUserEmail(String token) {
    String username = jwtUtil.extractUsername(token);
    if (username == null) {
      throw new BadCredentialsException(messageConfig.getMessage(MSG_TOKEN_NOT_VALID));
    }
    return username;
  }

  protected abstract void validateToken(String token, String userEmail);

  protected abstract Integer getUserId(String userEmail);
}
