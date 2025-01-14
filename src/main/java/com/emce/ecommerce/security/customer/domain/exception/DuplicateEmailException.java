package com.emce.ecommerce.security.customer.domain.exception;

public class DuplicateEmailException extends CustomerDomainException {

    private final String email;

    public DuplicateEmailException(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}