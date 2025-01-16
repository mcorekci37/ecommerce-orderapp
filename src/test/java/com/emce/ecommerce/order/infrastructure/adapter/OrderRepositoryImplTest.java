package com.emce.ecommerce.order.infrastructure.adapter;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.infrastructure.entity.OrderEntity;
import com.emce.ecommerce.order.infrastructure.mapper.OrderEntityMapper;
import com.emce.ecommerce.order.infrastructure.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static com.emce.ecommerce.order.TestUtil.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderRepositoryImplTest {

    @Mock
    private OrderEntity mockOrderEntity;
    @Mock
    private OrderJpaRepository jpaRepository;
    @Mock
    private OrderEntityMapper mapper;
    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldSucceed() {
        Order order = mock(Order.class);
        when(mapper.orderToOrderEntity(order)).thenReturn(mockOrderEntity);
        when(jpaRepository.save(mockOrderEntity)).thenReturn(mockOrderEntity);
        when(mapper.orderEntityToOrder(mockOrderEntity)).thenReturn(order);

        Order savedOrder = orderRepository.save(order);

        assertEquals(order, savedOrder);
        verify(mapper).orderToOrderEntity(order);
        verify(jpaRepository).save(mockOrderEntity);
        verify(mapper).orderEntityToOrder(mockOrderEntity);
    }

    @Test
    void findByOrderId_shouldSucceed() {
        OrderId orderId = new OrderId("1");
        Order mockOrder = mock(Order.class);
        OrderEntity mockOrderEntity = mock(OrderEntity.class);

        when(jpaRepository.findById(orderId.getValue())).thenReturn(Optional.of(mockOrderEntity));
        when(mapper.orderEntityToOrder(mockOrderEntity)).thenReturn(mockOrder);

        Optional<Order> foundOrder = orderRepository.findByOrderId(orderId);

        assertTrue(foundOrder.isPresent());
        assertEquals(mockOrder, foundOrder.get());
        verify(jpaRepository).findById(orderId.getValue());
    }
    @Test
    void findByUsernameAndCreatedAtBetweenAndTotalPriceBetween_shouldReturnPageOrder() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        BigDecimal min = BigDecimal.valueOf(10);
        BigDecimal max = BigDecimal.valueOf(10);
        Pageable pageable = PageRequest.of(0, 10);
        var orderEntityPage = mock(PageImpl.class);

        when(jpaRepository.findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(TEST_USER, start, end, min, max, pageable))
                .thenReturn(orderEntityPage);

        orderRepository.findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(TEST_USER, start, end, min, max, pageable);

        verify(jpaRepository, times(1)).findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(TEST_USER, start, end, min, max, pageable);
        System.out.println();
    }
}
