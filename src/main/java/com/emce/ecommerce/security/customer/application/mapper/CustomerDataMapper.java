package com.emce.ecommerce.security.customer.application.mapper;

import com.emce.ecommerce.security.customer.domain.entity.Customer;
import com.emce.ecommerce.security.customer.web.dto.RegisterRequest;
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
