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
      if (request.getServletPath().contains(WHITE_LIST)) {
        filterChain.doFilter(request, response);
        return;
      }
      String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      final String userEmail;
      if (authHeader == null || !authHeader.startsWith(BEARER)) {
        filterChain.doFilter(request, response);
        return;
      }
      String jwt = authHeader.substring(BEARER.length());
      userEmail = jwtUtil.extractUsername(jwt);
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        var isTokenValid =
            tokenRepository
                .findByToken(jwt)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        if (jwtUtil.isTokenValid(jwt, userDetails.getUsername()) && isTokenValid) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          response.getWriter().write(messageConfig.getMessage(MSG_AUTHENTICATION_FAILED));
          response.getWriter().flush();
        }
      }
      filterChain.doFilter(request, response);

    } catch (UsernameNotFoundException e) {
      // Handle UsernameNotFoundException when user associated with JWT is deleted
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(messageConfig.getMessage(MSG_USER_NOT_FOUND));
      response.getWriter().flush();
    } catch (Exception e) {
      // Catch other exceptions if necessary
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(messageConfig.getMessage(MSG_AUTHENTICATION_FAILED));
      response.getWriter().flush();
    }
  }
}
