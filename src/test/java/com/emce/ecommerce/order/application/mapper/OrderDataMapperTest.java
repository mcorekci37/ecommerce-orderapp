package com.emce.ecommerce.order.application.mapper;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.order.web.dto.OrderRequest;
import com.emce.ecommerce.order.web.dto.OrderResponse;
import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderDataMapperTest {

    @InjectMocks
    private OrderDataMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void orderRequestToOrder_ShouldMapCorrectly() {
        // Arrange
        String username = "testUser";
        int quantity = 2;
        BigDecimal totalAmount = new BigDecimal("100.00");
        Product product = new Product(1, "name", new Category("Test Product"),
            new Seller(new SellerId(1)), Money.of(50), 10L);
        OrderRequest orderRequest = new OrderRequest(1, quantity, totalAmount);

        // Act
        Order order = mapper.orderRequestToOrder(orderRequest, product, username);

        // Assert
        assertNotNull(order);
        assertEquals(username, order.getUsername());
        assertEquals(product, order.getProduct());
        assertEquals(quantity, order.getQuantity());
        assertEquals(new Money(totalAmount), order.getTotalPrice());
    }

    @Test
    void orderToOrderResponse_ShouldMapCorrectly() {
        // Arrange
        String username = "testUser";
        String productName = "Test Product";
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(1, productName, new Category("Test Product"),
                new Seller(new SellerId(1)), Money.of(50), 10L);
        Money totalPrice = new Money(new BigDecimal(100));
        String orderid = "orderid1";
        Order order = new Order(new OrderId(orderid), username, product, 2, totalPrice, now, now, OrderStatus.CREATED);

        // Act
        OrderResponse response = mapper.orderToOrderResponse(order);

        // Assert
        assertNotNull(response);
        assertEquals(username, response.username());
        assertEquals(orderid, response.orderId());
        assertEquals(1, response.productId());
        assertEquals(productName, response.productName());
        assertEquals(new BigDecimal(50.), response.unitPrice());
        assertEquals(2, response.quantity());
        assertEquals(new BigDecimal(100), response.totalPrice());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
        assertEquals(OrderStatus.CREATED.toString(), response.orderStatus());
    }
}
