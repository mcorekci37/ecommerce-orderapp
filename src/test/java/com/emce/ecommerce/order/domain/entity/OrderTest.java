package com.emce.ecommerce.order.domain.entity;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.exception.ShippedOrderCannotBeCancelledException;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static com.emce.ecommerce.order.TestUtil.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OrderTest {

    @Test
    void cancelCreatedOrderSuccessfully(){
        long stock = 98L;
        LocalDateTime now = LocalDateTime.now();
        int quantity = 2;
        Order order = new Order(new OrderId("1"), "user", new Product("", new Category(""), mock(Seller.class), Money.of(50), stock), quantity, Money.of(100),now,now, OrderStatus.CREATED);
        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(stock+quantity, order.getProduct().getStock());
    }

    @Test
    void cancelShippedOrder_shouldThrowShippedOrderCannotBeCancelledException(){
        long stock = 98L;
        LocalDateTime now = LocalDateTime.now();
        int quantity = 2;
        String orderId = "1";
        String productName = "product";
        String categoryName = "category";
        Money price = Money.of(50);
        Product product = new Product(productName, new Category(categoryName), mock(Seller.class), price, stock);
        Money totalPrice = Money.of(100);
        Order order = new Order(new OrderId(orderId), TEST_USER, product, quantity, totalPrice,now,now, OrderStatus.SHIPPED);

        assertThrows(ShippedOrderCannotBeCancelledException.class, () -> order.cancel());

        assertEquals(OrderStatus.SHIPPED, order.getOrderStatus());
        assertEquals(stock, order.getProduct().getStock());
        assertEquals(TEST_USER, order.getUsername());
        assertEquals(quantity, order.getQuantity());
        assertEquals(totalPrice, order.getTotalPrice());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
    }
}
