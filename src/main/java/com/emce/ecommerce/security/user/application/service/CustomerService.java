package com.emce.ecommerce.security.user.application.service;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.security.user.domain.exception.DuplicateEmailException;
import com.emce.ecommerce.security.auth.util.JwtUtil;
import com.emce.ecommerce.security.token.Token;
import com.emce.ecommerce.security.token.TokenRepository;
import com.emce.ecommerce.security.token.TokenType;
import com.emce.ecommerce.security.user.application.mapper.CustomerDataMapper;
import com.emce.ecommerce.security.user.domain.exception.UserNotFoundException;
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
import org.springframework.stereotype.Service;

import static com.emce.ecommerce.common.domain.config.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerDataMapper dataMapper;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MessageConfig messageConfig;


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
            throw new DuplicateEmailException(customer.getEmail());
        }
    }

    public AuthResponse login(AuthRequest request) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var customer = getCustomer(request.email());

        var jwtToken = jwtUtil.generateToken(customer.getEmail());
        saveUserToken(customer.getId().getValue(), jwtToken);
        return AuthResponse.builder()
                .token(jwtToken)
                .expiresAt(jwtUtil.extractExpiration(jwtToken))
                .build();
    }

    public Integer validateToken(String token) {
        final String userEmail = jwtUtil.extractUsername(token);

        if (userEmail != null) {
            Customer customer = getCustomer(userEmail);
            ;
            if (!jwtUtil.isTokenValid(token, customer.getEmail())) {
                throw new CredentialsExpiredException(messageConfig.getMessage(MSG_TOKEN_EXPIRED));
            }else {
                return customer.getId().getValue();
            }
        }
        throw new BadCredentialsException(messageConfig.getMessage(MSG_TOKEN_NOT_VALID));
    }

    private Customer getCustomer(String userEmail) {
        Customer customer = customerRepository.findByEmail(userEmail)
                .orElseThrow(
                () -> new UserNotFoundException(userEmail));
        return customer;
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
