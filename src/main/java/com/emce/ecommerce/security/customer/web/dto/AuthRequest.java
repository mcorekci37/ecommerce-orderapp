package com.emce.ecommerce.security.customer.web.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequest(
    @NotEmpty(message = "{MSG_MAIL_EMPTY}") String email,
    @NotEmpty(message = "{MSG_PW_EMPTY}") String password) {}
