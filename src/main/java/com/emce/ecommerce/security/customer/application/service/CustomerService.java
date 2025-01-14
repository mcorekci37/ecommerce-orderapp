package com.emce.ecommerce.security.customer.application.service;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.security.customer.domain.exception.CustomerDomainException;
import com.emce.ecommerce.security.customer.domain.exception.DuplicateEmailException;
import com.emce.ecommerce.security.auth.util.JwtUtil;
import com.emce.ecommerce.security.token.Token;
import com.emce.ecommerce.security.token.TokenRepository;
import com.emce.ecommerce.security.token.TokenType;
import com.emce.ecommerce.security.customer.application.mapper.CustomerDataMapper;
import com.emce.ecommerce.security.customer.domain.exception.CustomerNotFoundException;
import com.emce.ecommerce.security.customer.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.customer.infrastructure.entity.CustomerEntity;
import com.emce.ecommerce.security.customer.web.dto.AuthRequest;
import com.emce.ecommerce.security.customer.web.dto.AuthResponse;
import com.emce.ecommerce.security.customer.web.dto.RegisterRequest;
import com.emce.ecommerce.security.customer.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            Customer savedCustomer = saveCustomer(customer);
            var jwtToken = jwtUtil.generateToken(savedCustomer.getEmail());
            saveUserToken(savedCustomer.getId().getValue(), jwtToken);
            log.info("User created and token is generated for customer {}", savedCustomer.getEmail());

            return AuthResponse.builder()
                    .token(jwtToken)
                    .expiresAt(jwtUtil.extractExpiration(jwtToken))
                    .build();
        } catch (DataIntegrityViolationException e) {
            log.info("Customer with {} mail is already exists.", registerRequest.email());
            throw new DuplicateEmailException(customer.getEmail());
        }
    }

    private Customer saveCustomer(Customer customer) {
        Customer customerResult = customerRepository.save(customer);
        if (customerResult == null) {
            log.error("Could not save customer {}!", customer.getEmail());
            throw new CustomerDomainException(messageConfig.getMessage(MSG_ERR_COULD_NOT_SAVE_CUSTOMER));
        }
        return customerResult;
    }


    public AuthResponse login(AuthRequest request) throws AuthenticationException {
        log.debug("Authentication is in process.");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        log.debug("Authentication established.");

        var customer = getCustomer(request.email());

        var jwtToken = jwtUtil.generateToken(customer.getEmail());
        saveUserToken(customer.getId().getValue(), jwtToken);
        log.info("Token is generated for customer {}", customer.getEmail());
        return AuthResponse.builder()
                .token(jwtToken)
                .expiresAt(jwtUtil.extractExpiration(jwtToken))
                .build();
    }

    public Integer validateToken(String token) {
        log.info("Token validation process is started.");
        final String userEmail = jwtUtil.extractUsername(token);

        if (userEmail != null) {
            Customer customer = getCustomer(userEmail);

            if (!jwtUtil.isTokenValid(token, customer.getEmail())) {
                log.info("Token is not valid.");
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
                () -> {
                    log.info("Customer not found with {}", userEmail);
                    return new CustomerNotFoundException(userEmail);
                }
                );
        log.debug("Customer found with {}", userEmail);
        return customer;
    }

    private Token saveUserToken(Integer customerId, String jwtToken) {
    var token =
        Token.builder()
            .customer(new CustomerEntity(customerId))
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        Token savedToken = saveToken(token);
        return savedToken;
    }

    private Token saveToken(Token token) {
        Token savedToken = tokenRepository.save(token);

        if (savedToken == null) {
            log.error("Could not save token {}!", token.getCustomer().getId());
            throw new CustomerDomainException(messageConfig.getMessage(MSG_ERR_COULD_NOT_SAVE_TOKEN));
        }
        return savedToken;
    }
}
