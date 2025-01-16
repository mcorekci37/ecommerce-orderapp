package com.emce.ecommerce.order.infrastructure.mapper;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.order.infrastructure.entity.OrderEntity;
import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import com.emce.ecommerce.product.infrastructure.entity.CategoryEntity;
import com.emce.ecommerce.product.infrastructure.entity.ProductEntity;
import com.emce.ecommerce.product.infrastructure.mapper.ProductEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.emce.ecommerce.order.TestUtil.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderEntityMapperTest {

    @InjectMocks
    private OrderEntityMapper orderEntityMapper;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void orderEntityToOrder_ShouldMapCorrectly() {
    // Arrange
        CategoryEntity categoryEntity = new CategoryEntity();
        BigDecimal price = BigDecimal.valueOf(50.00);
        ProductEntity productEntity = new ProductEntity(1, "Product", categoryEntity, 1, price, 10L);
        String orderId = "123L";
        BigDecimal totalPrice = BigDecimal.valueOf(100.00);
        int quantity = 2;
        OrderEntity orderEntity = new OrderEntity(
                orderId,
                TEST_USER,
                productEntity,
                quantity,
                totalPrice,
                OrderStatus.CREATED
        );
        Category category = new Category("category");
        Seller seller = new Seller(new SellerId(1));
        Product product = new Product(1, "Product", category, seller, new Money(price), 10L);

        when(productEntityMapper.productEntityToProduct(productEntity)).thenReturn(product);

        // Act
        Order order = orderEntityMapper.orderEntityToOrder(orderEntity);

        // Assert
        assertNotNull(order);
        assertEquals(new OrderId(orderId), order.getId());
        assertEquals(TEST_USER, order.getUsername());
        assertEquals(product, order.getProduct());
        assertEquals(quantity, order.getQuantity());
        assertEquals(new Money(totalPrice), order.getTotalPrice());
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());

        verify(productEntityMapper).productEntityToProduct(productEntity);
    }

    @Test
    void orderToOrderEntity_ShouldMapCorrectly() {
        // Arrange
        Category category = new Category("category");
        Seller seller = new Seller(new SellerId(1));
        BigDecimal price = BigDecimal.valueOf(50.00);
        String orderId = "123L";
        Product product = new Product(1, "Product", category, seller, new Money(price), 10L);
        int quantity = 2;
        BigDecimal totalPrice = BigDecimal.valueOf(100.00);
        Order order = new Order(
                new OrderId(orderId),
                TEST_USER,
                product,
                quantity,
                new Money(totalPrice),
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0),
                OrderStatus.CREATED
        );
        CategoryEntity categoryEntity = new CategoryEntity();
        ProductEntity productEntity = new ProductEntity(1, "Product", categoryEntity, 1, price, 10L);

        when(productEntityMapper.productToProductEntity(product)).thenReturn(productEntity);

        // Act
        OrderEntity orderEntity = orderEntityMapper.orderToOrderEntity(order);

        // Assert
        assertNotNull(orderEntity);
        assertEquals(orderId, orderEntity.getId());
        assertEquals(TEST_USER, orderEntity.getUsername());
        assertEquals(productEntity, orderEntity.getProduct());
        assertEquals(quantity, orderEntity.getQuantity());
        assertEquals(totalPrice, orderEntity.getTotalPrice());
        assertEquals(OrderStatus.CREATED, orderEntity.getOrderStatus());

        verify(productEntityMapper).productToProductEntity(product);
    }
}
