package com.emce.ecommerce.security.auth.config;

import com.emce.ecommerce.security.auth.util.JwtUtil;
import com.emce.ecommerce.security.customer.domain.valueobjects.Role;
import com.emce.ecommerce.security.customer.infrastructure.entity.CustomerEntity;
import com.emce.ecommerce.security.token.Token;
import com.emce.ecommerce.security.token.TokenRepository;
import com.emce.ecommerce.security.auth.service.CustomUserDetailsService;
import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.security.token.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class JwtAuthenticationFilterTest {

    public static final String MESSAGE = "MESSAGE";
    @Mock private JwtUtil jwtUtil;
    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private TokenRepository tokenRepository;
    @Mock private MessageConfig messageConfig;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        when(messageConfig.getMessage(any())).thenReturn(MESSAGE);
        when(messageConfig.getMessage(any(), any())).thenReturn(MESSAGE);

//        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldBypassWhiteListedUrl() throws Exception {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/auth");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateWithValidToken() throws Exception {
        // Arrange
        String token = "Bearer validToken";
        String username = "user@example.com";
        CustomerEntity customerEntity = new CustomerEntity("testuser", "test@mail.com", "Pw1.", Role.USER);
        when(request.getHeader("Authorization")).thenReturn(token);
        when(request.getServletPath()).thenReturn("/api/v1/order");
        when(jwtUtil.extractUsername("validToken")).thenReturn(username);
        when(jwtUtil.isTokenValid(any(), any())).thenReturn(true);
        Token validToken = new Token(1, "validToken", TokenType.BEARER, false, false, new CustomerEntity());
        when(tokenRepository.findByToken(any()))
            .thenReturn(
                Optional.of(
                        validToken));
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(customerEntity);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        customerEntity, null, customerEntity.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));



        // Mocking SecurityContextHolder to capture the interaction
         SecurityContext securityContext = mock(SecurityContext.class);
         try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
             mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
             // Act
              jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
              // Assert
              ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
              verify(securityContext, times(1)).setAuthentication(authToken);
              assertEquals(customerEntity, authToken.getPrincipal());
              verify(filterChain).doFilter(request, response);
         }
    }

    @Test
    void shouldReturnForbiddenWhenTokenIsInvalid() throws Exception {
        // Arrange
        String token = "Bearer invalidToken";
        String username = "user@example.com";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("invalidToken")).thenReturn(username);
        when(jwtUtil.isTokenValid(any(), any())).thenReturn(false);

        Token invalidToken = new Token(1, "invalidToken", TokenType.BEARER, true, true, new CustomerEntity());
        when(tokenRepository.findByToken(any())).thenReturn(java.util.Optional.of(invalidToken));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter(), times(1)).write(anyString());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
        // Arrange
        String token = "Bearer validToken";
        String username = "user@example.com";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("validToken")).thenReturn(username);
        when(jwtUtil.isTokenValid(any(), any())).thenReturn(true);
        when(tokenRepository.findByToken(any())).thenReturn(java.util.Optional.empty());

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter(), times(1)).write(anyString());
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthHeaderIsMissing() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
    }

    @Test
    void shouldHandleExceptionWhenAuthenticationFails() throws Exception {
        // Arrange
        String token = "Bearer validToken";
        String username = "user@example.com";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("validToken")).thenReturn(username);
        when(jwtUtil.isTokenValid(any(), any())).thenThrow(new RuntimeException("Authentication failed"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter(), times(1)).write(MESSAGE);
    }
}
