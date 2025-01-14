package com.emce.ecommerce.security.auth.service;

import com.emce.ecommerce.security.token.Token;
import com.emce.ecommerce.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private static final String BEARER = "Bearer ";

  private final TokenRepository tokenRepository;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith(BEARER)) {
      return;
    }
    String tokenStr = authHeader.substring(BEARER.length());
    Token token = tokenRepository.findByToken(tokenStr).orElse(null);
    if (token != null) {
      token.setExpired(true);
      token.setRevoked(true);
      tokenRepository.save(token);
      SecurityContextHolder.clearContext();
    }
  }
}
