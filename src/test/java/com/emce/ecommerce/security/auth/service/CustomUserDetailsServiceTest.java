package com.emce.ecommerce.security.auth.service;

import com.emce.ecommerce.security.customer.domain.entity.Customer;
import com.emce.ecommerce.security.customer.domain.exception.CustomerNotFoundException;
import com.emce.ecommerce.security.customer.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.customer.infrastructure.entity.CustomerEntity;
import com.emce.ecommerce.security.customer.infrastructure.mapper.CustomerEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerEntityMapper entityMapper;

    private final String TEST_USERNAME = "testuser@example.com";

    @Mock
    private CustomerEntity customerEntity;

    @Mock
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_CustomerFound() {
        // Arrange
        when(customer.getEmail()).thenReturn(TEST_USERNAME);
        when(customerEntity.getEmail()).thenReturn(TEST_USERNAME);
        when(customerRepository.findByEmail(TEST_USERNAME)).thenReturn(Optional.of(customer));
        when(entityMapper.customerToCustomerEntity(customer)).thenReturn(customerEntity);

        // Act
        CustomerEntity result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getEmail());
        verify(customerRepository, times(1)).findByEmail(TEST_USERNAME);
    }

    @Test
    void testLoadUserByUsername_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByEmail(TEST_USERNAME)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(TEST_USERNAME));

        assertEquals(TEST_USERNAME, exception.getUsername());
        verify(customerRepository, times(1)).findByEmail(TEST_USERNAME);
    }
}
