package com.emce.ecommerce.security.auth.config;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.security.auth.util.JwtUtil;
import com.emce.ecommerce.security.token.TokenRepository;
import com.emce.ecommerce.security.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.emce.ecommerce.common.domain.config.MessageConstants.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String WHITE_LIST = "/api/v1/auth";
  public static final String BEARER = "Bearer ";

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService customUserDetailsService;
  private final TokenRepository tokenRepository;
  private final MessageConfig messageConfig;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException {

    try {
      if (isWhiteListed(request)) {
        log.debug("White listed URL requested.");
        filterChain.doFilter(request, response);
        return;
      }
      String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (!isValid(authHeader)) {
        log.info("Auth header is invalid.");
        filterChain.doFilter(request, response);
        return;
      }
      extractTokenAndAuthorize(request, response, authHeader);
      filterChain.doFilter(request, response);

    } catch (UsernameNotFoundException e) {
      // Handle UsernameNotFoundException when user associated with JWT is deleted
      log.debug("user associated with JWT is not found.");
      prepareTokenInvalidMessage(response, HttpServletResponse.SC_UNAUTHORIZED, MSG_USER_NOT_FOUND);
    } catch (Exception e) {
      // Catch other exceptions if necessary
      log.error("Error when authenticating user. {}", e.getMessage());
      prepareTokenInvalidMessage(response, HttpServletResponse.SC_UNAUTHORIZED, MSG_AUTHENTICATION_FAILED);
    }
  }

  private void extractTokenAndAuthorize(HttpServletRequest request, HttpServletResponse response, String authHeader) throws IOException {
    String jwt = authHeader.substring(BEARER.length());
    final String userEmail = jwtUtil.extractUsername(jwt);
    if (authenticationValid(userEmail)) {
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

      if (jwtUtil.isTokenValid(jwt, userDetails.getUsername()) && isTokenValid(jwt)) {
        log.debug("Token is valid. {}", jwt);
        setAuthentication(request, userDetails);
      } else {
        log.debug("Token is invalid");
        prepareTokenInvalidMessage(response, HttpServletResponse.SC_FORBIDDEN, MSG_AUTHENTICATION_FAILED);
      }
    }
  }

  private void prepareTokenInvalidMessage(HttpServletResponse response, int scForbidden, String msgAuthenticationFailed) throws IOException {
    response.setStatus(scForbidden);
    response.getWriter().write(messageConfig.getMessage(msgAuthenticationFailed));
    response.getWriter().flush();
  }

  private static void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  private Boolean isTokenValid(String jwt) {
    return tokenRepository
            .findByToken(jwt)
            .map(t -> !t.isExpired() && !t.isRevoked())
            .orElse(false);
  }

  private static boolean authenticationValid(String userEmail) {
    return userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null;
  }

  private static boolean isValid(String authHeader) {
    return authHeader != null && authHeader.startsWith(BEARER);
  }

  private static boolean isWhiteListed(HttpServletRequest request) {
    return request.getServletPath().contains(WHITE_LIST);
  }
}
