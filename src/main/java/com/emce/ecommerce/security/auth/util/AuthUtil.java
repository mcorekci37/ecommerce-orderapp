package com.emce.ecommerce.security.auth.util;

import com.emce.ecommerce.security.customer.domain.valueobjects.Role;
import com.emce.ecommerce.security.customer.infrastructure.entity.CustomerEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static String getUsername() {
        return getPrincipal().getEmail();
    }

    public static Role getRole() {
        return getPrincipal().getRole();
    }

    public static CustomerEntity getPrincipal() {
        return (CustomerEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
