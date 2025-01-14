package com.emce.ecommerce.security.user.web.dto;

import java.util.Date;
import lombok.Builder;

@Builder
public record AuthResponse(String token, Date expiresAt) {}
