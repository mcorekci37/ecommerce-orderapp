package com.emce.ecommerce.security.user.web.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequest(
    @NotEmpty(message = "email should not be empty") String email,
    @NotEmpty(message = "password should not be empty") String password) {}
