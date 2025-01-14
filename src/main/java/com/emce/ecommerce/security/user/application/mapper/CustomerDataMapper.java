package com.emce.ecommerce.security.user.application.mapper;

import com.emce.ecommerce.security.user.domain.entity.Customer;
import com.emce.ecommerce.security.user.web.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDataMapper {

    private final PasswordEncoder passwordEncoder;

    public Customer registerRequestToCustomer(RegisterRequest registerRequest) {
        return new Customer(
                registerRequest.name(),
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()));
    }

}
