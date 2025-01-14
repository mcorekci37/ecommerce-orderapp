package com.emce.ecommerce.security.user.web.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequest(
    @NotEmpty(message = "{MSG_MAIL_EMPTY}") String email,
    @NotEmpty(message = "{MSG_PW_EMPTY}") String password) {}
