package com.emce.ecommerce.security.auth.service;

import com.emce.ecommerce.security.token.Token;
import com.emce.ecommerce.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LogoutServiceTest {

    @InjectMocks
    private LogoutService logoutService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private Token token;

    private final String TEST_TOKEN = "testToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        token = new Token();
        token.setToken(TEST_TOKEN);
    }

    @Test
    void testLogout_TokenFound() {
        // Arrange
        String authHeader = "Bearer " + TEST_TOKEN;
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(java.util.Optional.of(token));

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        assertTrue(token.isExpired());
        assertTrue(token.isRevoked());
        verify(tokenRepository, times(1)).save(token);
        verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION);
        verifyNoInteractions(response, authentication);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testLogout_TokenNotFound() {
        // Arrange
        String authHeader = "Bearer " + TEST_TOKEN;
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(java.util.Optional.empty());

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, times(1)).findByToken(TEST_TOKEN);
        verifyNoInteractions(response, authentication);
        verifyNoMoreInteractions(tokenRepository);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.verify(() -> SecurityContextHolder.clearContext(), never());
        }
    }

    @Test
    void testLogout_NoAuthorizationHeader() {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verifyNoInteractions(tokenRepository, response, authentication);
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.verify(() -> SecurityContextHolder.clearContext(), never());
        }
    }

    @Test
    void testLogout_InvalidAuthorizationHeader() {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verifyNoInteractions(tokenRepository, response, authentication);
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.verify(() -> SecurityContextHolder.clearContext(), never());
        }
    }
}
