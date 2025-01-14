package com.emce.ecommerce.security.user.infrastructure.adapter;

import com.emce.ecommerce.security.user.domain.entity.Customer;
import com.emce.ecommerce.security.user.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.user.domain.valueobjects.CustomerId;
import com.emce.ecommerce.security.user.infrastructure.mapper.CustomerEntityMapper;
import com.emce.ecommerce.security.user.infrastructure.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerEntityMapper mapper;

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::customerEntityToCustomer);
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return jpaRepository.findById(customerId.getValue()).map(mapper::customerEntityToCustomer);
    }

    @Override
    public Customer save(Customer customer) {
        return mapper.customerEntityToCustomer(jpaRepository.save(mapper.customerToCustomerEntity(customer)));
    }
}
