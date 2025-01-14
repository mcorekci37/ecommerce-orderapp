package com.emce.ecommerce.security.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotEmpty(message = "{MSG_NAME_EMPTY}") String name,
    @Email(message = "{MSG_INVALID_MAIL_FORMAT}") @NotEmpty(message = "{MSG_MAIL_EMPTY}")
        String email,
    @NotEmpty(message = "{MSG_PW_EMPTY}")
        @Size(min = 8, message = "{MSG_PW_LENGTH}")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.]).*$",
            message = "{MSG_MAIL_PATTERN}")
        String password) {}
