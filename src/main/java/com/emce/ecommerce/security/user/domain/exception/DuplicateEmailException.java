package com.emce.ecommerce.security.user.domain.exception;

public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}