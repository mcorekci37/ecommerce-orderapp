package com.emce.ecommerce.order;

import com.emce.ecommerce.security.customer.domain.valueobjects.Role;
import com.emce.ecommerce.security.customer.infrastructure.entity.CustomerEntity;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {

    public static final String TEST_USER = "test-user";
    public static final String TEST_ADMIN_USER = "test-admin-user";

    public static void mockTestUser() {
        mockUser(TEST_USER, Role.USER);
    }
    public static void mockAdminUser() {
        mockUser(TEST_ADMIN_USER, Role.ADMIN);
    }

    private static void mockUser(String username, Role role) {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        CustomerEntity mockPrincipal = mock(CustomerEntity.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockPrincipal); // Replace with a valid username
        when(mockPrincipal.getEmail()).thenReturn(username);
        when(mockPrincipal.getRole()).thenReturn(role);
        SecurityContextHolder.setContext(securityContext);
    }
}
