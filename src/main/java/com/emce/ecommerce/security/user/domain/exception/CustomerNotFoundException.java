package com.emce.ecommerce.security.user.domain.exception;

public class CustomerNotFoundException extends CustomerDomainException {

    private final String username;

    public CustomerNotFoundException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}