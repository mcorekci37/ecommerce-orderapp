package com.emce.ecommerce.security.customer.web.controller;

import com.emce.ecommerce.security.customer.application.service.CustomerService;
import com.emce.ecommerce.security.customer.web.dto.AuthRequest;
import com.emce.ecommerce.security.customer.web.dto.AuthResponse;
import com.emce.ecommerce.security.customer.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Tag(name = "Customer Authentication Management", description = "APIs for managing customer authentication")
public class CustomerController {
  private final CustomerService customerService;

  @PostMapping("/register")
  @Operation(summary = "Register a new customer", description = "Registers a new customer to the system and returns jwt token")
  public ResponseEntity<AuthResponse> registerUser(
      @Valid @RequestBody RegisterRequest registerRequest) {
    log.info("user register request started for user :{}", registerRequest.email());
    AuthResponse response = customerService.register(registerRequest);
    log.info("user register request finished for user :{}", registerRequest.email());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @Operation(summary = "Logs in a customer", description = "Logs in customer to the system and returns jwt token")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    log.info("user login request started for user :{}", request.email());
    AuthResponse response = customerService.login(request);
    log.info("user login request finished for user :{}", request.email());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  @Operation(summary = "Validates customer's token", description = "Validates customer's token and returns customer id that it belongs to")
  public ResponseEntity<Integer> validateToken(@RequestParam("token") String token) {
    log.info("user validation request started for token :{}", token);
    Integer userId = customerService.validateToken(token);
    log.info("user validation request finished for token :{} and user id is {}", token, userId);
    return ResponseEntity.ok(userId);
  }
}
