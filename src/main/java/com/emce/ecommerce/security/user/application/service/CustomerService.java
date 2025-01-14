package com.emce.ecommerce.security.user.application.service;

import com.emce.ecommerce.security.user.domain.exception.DuplicateEmailException;
import com.emce.ecommerce.security.auth.util.JwtUtil;
import com.emce.ecommerce.security.auth.service.CustomUserDetailsService;
import com.emce.ecommerce.security.token.Token;
import com.emce.ecommerce.security.token.TokenRepository;
import com.emce.ecommerce.security.token.TokenType;
import com.emce.ecommerce.security.user.application.mapper.CustomerDataMapper;
import com.emce.ecommerce.security.user.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.user.infrastructure.entity.CustomerEntity;
import com.emce.ecommerce.security.user.web.dto.AuthRequest;
import com.emce.ecommerce.security.user.web.dto.AuthResponse;
import com.emce.ecommerce.security.user.web.dto.RegisterRequest;
import com.emce.ecommerce.security.user.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.emce.ecommerce.security.auth.service.CustomUserDetailsService.USER_NOT_FOUND_MSG;

@Service
@RequiredArgsConstructor
public class CustomerService {
    public static final String TOKEN_NOT_VALID_MSG = "Token cannot be validated";
    public static final String TOKEN_EXPIRED_MSG = "Token is expired or invalid";

    private final CustomerRepository customerRepository;
    private final CustomerDataMapper dataMapper;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    public AuthResponse register(RegisterRequest registerRequest) {
        var customer = dataMapper.registerRequestToCustomer(registerRequest);
        try {
            Customer savedCustomer = customerRepository.save(customer);
            var jwtToken = jwtUtil.generateToken(savedCustomer.getEmail());
            saveUserToken(savedCustomer.getId().getValue(), jwtToken);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .expiresAt(jwtUtil.extractExpiration(jwtToken))
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(String.format(CustomUserDetailsService.EMAIL_ALREADY_EXISTS_MSG, customer.getEmail()));
        }
    }

    public AuthResponse login(AuthRequest request) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    var user =
        customerRepository
            .findByEmail(request.email())
            .orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, request.email())));

        var jwtToken = jwtUtil.generateToken(user.getEmail());
        saveUserToken(user.getId().getValue(), jwtToken);
        return AuthResponse.builder()
                .token(jwtToken)
                .expiresAt(jwtUtil.extractExpiration(jwtToken))
                .build();
    }

    public Integer validateToken(String token) {
        final String userEmail = jwtUtil.extractUsername(token);

        if (userEmail != null) {
            Customer customer = customerRepository.findByEmail(userEmail)
                    .orElseThrow(
                    () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, userEmail)));
            ;
            if (!jwtUtil.isTokenValid(token, customer.getEmail())) {
                throw new CredentialsExpiredException(TOKEN_EXPIRED_MSG);
            }else {
                return customer.getId().getValue();
            }
        }
        throw new BadCredentialsException(TOKEN_NOT_VALID_MSG);
    }
    private void saveUserToken(Integer customerId, String jwtToken) {
    var token =
        Token.builder()
            .customer(new CustomerEntity(customerId))
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

}
