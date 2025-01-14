package com.emce.ecommerce.security.user.web.controller;

import com.emce.ecommerce.security.user.application.service.CustomerService;
import com.emce.ecommerce.security.user.web.dto.AuthRequest;
import com.emce.ecommerce.security.user.web.dto.AuthResponse;
import com.emce.ecommerce.security.user.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CustomerController {
  private final CustomerService customerService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(
      @Valid @RequestBody RegisterRequest registerRequest) {
    return new ResponseEntity<>(customerService.register(registerRequest), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(customerService.login(request));
  }

  @PostMapping("/validate")
  public ResponseEntity<Integer> validateToken(@RequestParam("token") String token) {
    Integer userId = customerService.validateToken(token);
    return ResponseEntity.ok(userId);
  }
}
