package com.emce.ecommerce.security.user.web.controller;

import com.emce.ecommerce.security.user.application.service.CustomerService;
import com.emce.ecommerce.security.user.web.dto.AuthRequest;
import com.emce.ecommerce.security.user.web.dto.AuthResponse;
import com.emce.ecommerce.security.user.web.dto.RegisterRequest;
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
public class CustomerController {
  private final CustomerService customerService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(
      @Valid @RequestBody RegisterRequest registerRequest) {
    log.info("user register request started for user :{}", registerRequest.email());
    AuthResponse response = customerService.register(registerRequest);
    log.info("user register request finished for user :{}", registerRequest.email());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    log.info("user login request started for user :{}", request.email());
    AuthResponse response = customerService.login(request);
    log.info("user login request finished for user :{}", request.email());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  public ResponseEntity<Integer> validateToken(@RequestParam("token") String token) {
    log.info("user validation request started for token :{}", token);
    Integer userId = customerService.validateToken(token);
    log.info("user validation request finished for token :{} and user id is {}", token, userId);
    return ResponseEntity.ok(userId);
  }
}
