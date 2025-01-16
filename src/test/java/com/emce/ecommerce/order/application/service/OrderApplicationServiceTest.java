package com.emce.ecommerce.order.application.service;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.order.TestUtil;
import com.emce.ecommerce.order.application.mapper.OrderDataMapper;
import com.emce.ecommerce.order.application.validator.OrderPriceValidator;
import com.emce.ecommerce.order.application.validator.OrderQuantityValidator;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.events.OrderEvent;
import com.emce.ecommerce.order.domain.exception.CannotCancelOtherUsersOrderException;
import com.emce.ecommerce.order.domain.exception.OrderNotFoundException;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.infrastructure.kafka.EventPublisher;
import com.emce.ecommerce.order.infrastructure.payment.PaymentHelper;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.exception.ProductNotFoundException;
import com.emce.ecommerce.security.customer.domain.exception.CustomerDomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.emce.ecommerce.order.TestUtil.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderApplicationServiceTest {

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderDataMapper mapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private MessageConfig messageConfig;

    @Mock
    private OrderQuantityValidator orderQuantityValidator;

    @Mock
    private OrderPriceValidator orderPriceValidator;

    @Mock
    private PaymentHelper paymentHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TestUtil.mockTestUser();
    }


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Arrange
        OrderRequest orderRequest = mock(OrderRequest.class);
        Order order = mock(Order.class);
        Order savedOrder = mock(Order.class);
        Product product = mock(Product.class);

        when(orderRequest.productId()).thenReturn(1);
        when(productRepository.findByProductId(any(ProductId.class))).thenReturn(Optional.of(product));
        when(mapper.orderRequestToOrder(orderRequest, product, TEST_USER)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(mapper.orderToOrderResponse(savedOrder)).thenReturn(mock(OrderResponse.class));
        when(order.getProduct()).thenReturn(product);
        when(product.getId()).thenReturn(new ProductId(1));
        when(productRepository.save(product)).thenReturn(product);

        // Act
        OrderResponse response = orderApplicationService.create(orderRequest);

        // Assert
        assertNotNull(response);
        verify(paymentHelper).processPayment(savedOrder);
        verify(eventPublisher).publish(any(OrderEvent.class));
    }

    @Test
    void createOrder_ShouldThrowProductNotFoundException() {
        // Arrange
        OrderRequest orderRequest = mock(OrderRequest.class);

        when(orderRequest.productId()).thenReturn(1);
        when(productRepository.findByProductId(any(ProductId.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderApplicationService.create(orderRequest));
    }

    @Test
    void createOrder_ShouldThrowCustomerDomainException_whenSaveOrderFails() {
        // Arrange
        OrderRequest orderRequest = mock(OrderRequest.class);
        Order order = mock(Order.class);
        Order savedOrder = mock(Order.class);
        Product product = mock(Product.class);

        when(orderRequest.productId()).thenReturn(1);
        when(productRepository.findByProductId(any(ProductId.class))).thenReturn(Optional.of(product));
        when(mapper.orderRequestToOrder(orderRequest, product, TEST_USER)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(null);
        when(mapper.orderToOrderResponse(savedOrder)).thenReturn(mock(OrderResponse.class));
        when(order.getProduct()).thenReturn(product);
        when(product.getId()).thenReturn(new ProductId(1));
        when(productRepository.save(product)).thenReturn(product);

        // Act & Assert
        assertThrows(CustomerDomainException.class, () -> orderApplicationService.create(orderRequest));
    }

    @Test
    void createOrder_ShouldThrowCustomerDomainException_whenSaveProductFails() {
        // Arrange
        OrderRequest orderRequest = mock(OrderRequest.class);
        Order order = mock(Order.class);
        Order savedOrder = mock(Order.class);
        Product product = mock(Product.class);

        when(orderRequest.productId()).thenReturn(1);
        when(productRepository.findByProductId(any(ProductId.class))).thenReturn(Optional.of(product));
        when(mapper.orderRequestToOrder(orderRequest, product, TEST_USER)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(mapper.orderToOrderResponse(savedOrder)).thenReturn(mock(OrderResponse.class));
        when(order.getProduct()).thenReturn(product);
        when(product.getId()).thenReturn(new ProductId(1));
        when(productRepository.save(product)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomerDomainException.class, () -> orderApplicationService.create(orderRequest));
    }

    @Test
    void listOrders_ShouldReturnPagedOrders() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        BigDecimal minAmount = BigDecimal.valueOf(100);
        BigDecimal maxAmount = BigDecimal.valueOf(500);
        Pageable pageable = mock(Pageable.class);

        Page<Order> ordersPage = mock(Page.class);
        when(orderRepository.findByUsernameAndCreatedAtBetweenAndTotalPriceBetween(
                eq(TEST_USER), eq(startDate), eq(endDate), eq(minAmount), eq(maxAmount), eq(pageable)))
                .thenReturn(ordersPage);
        when(ordersPage.map(any())).thenReturn(mock(Page.class));

        // Act
        Page<OrderResponse> result = orderApplicationService.listOrders(startDate, endDate, minAmount, maxAmount, pageable);

        // Assert
        assertNotNull(result);
    }

    @Test
    void cancelOrder_ShouldCancelOrderSuccessfully() {
        // Arrange
        String orderId = "testOrderId";
        Order order = mock(Order.class);
        Product product = mock(Product.class);
        Order savedOrder = mock(Order.class);

        when(orderRepository.findByOrderId(new OrderId(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(productRepository.save(product)).thenReturn(product);
        when(order.getProduct()).thenReturn(product);
        when(order.getUsername()).thenReturn(TEST_USER);
        when(order.getId()).thenReturn(new OrderId(orderId));

        when(mapper.orderToOrderResponse(savedOrder)).thenReturn(mock(OrderResponse.class));

        // Act
        OrderResponse response = orderApplicationService.cancelOrder(orderId);

        // Assert
        assertNotNull(response);
        verify(paymentHelper).withdrawPayment(savedOrder);
        verify(eventPublisher).publish(any(OrderEvent.class));
    }

    @Test
    void cancelOrder_ShouldCancelOrderSuccessfully_whenAdminUser() {
        // Arrange
        TestUtil.mockAdminUser();
        String orderId = "testOrderId";
        Order order = mock(Order.class);
        Product product = mock(Product.class);
        Order savedOrder = mock(Order.class);

        when(orderRepository.findByOrderId(new OrderId(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(productRepository.save(product)).thenReturn(product);
        when(order.getProduct()).thenReturn(product);
        when(order.getUsername()).thenReturn(TEST_USER);
        when(order.getId()).thenReturn(new OrderId(orderId));

        when(mapper.orderToOrderResponse(savedOrder)).thenReturn(mock(OrderResponse.class));

        // Act
        OrderResponse response = orderApplicationService.cancelOrder(orderId);

        // Assert
        assertNotNull(response);
        verify(paymentHelper).withdrawPayment(savedOrder);
        verify(eventPublisher).publish(any(OrderEvent.class));
    }

    @Test
    void cancelOrder_ShouldThrowOrderNotFoundException() {
        // Arrange
        String orderId = "testOrderId";

        when(orderRepository.findByOrderId(new OrderId(orderId))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderApplicationService.cancelOrder(orderId));
    }

    @Test
    void cancelOrder_ShouldThrowCannotCancelOtherUsersOrderException() {
        // Arrange
        String orderId = "testOrderId";
        Order order = mock(Order.class);

        when(orderRepository.findByOrderId(new OrderId(orderId))).thenReturn(Optional.of(order));
        when(order.getUsername()).thenReturn("anotherUser");

        // Act & Assert
        assertThrows(CannotCancelOtherUsersOrderException.class, () -> orderApplicationService.cancelOrder(orderId));
    }
}
