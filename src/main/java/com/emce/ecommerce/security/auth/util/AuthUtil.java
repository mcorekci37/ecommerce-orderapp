package com.emce.ecommerce.security.auth.util;

import com.emce.ecommerce.security.user.infrastructure.entity.CustomerEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static String getUsername() {
        return getPrincipal().getEmail();
    }

    public static CustomerEntity getPrincipal() {
        return (CustomerEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
