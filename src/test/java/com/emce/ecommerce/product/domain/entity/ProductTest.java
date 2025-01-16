package com.emce.ecommerce.product.domain.entity;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import com.emce.ecommerce.product.exception.OutOfStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void consumeStock_shouldThrowOutOfStockException(){
        Category category = new Category("category");
        Seller seller = new Seller(new SellerId(1));
        BigDecimal price = BigDecimal.valueOf(50.00);
        long stock = 10L;
        Product product = new Product(1, "Product", category, seller, new Money(price), stock);

        assertThrows(OutOfStockException.class, () -> product.consumeStock(11));

        assertEquals(stock, product.getStock());

    }

    @Test
    void consumeStock_shouldSucceed(){
        Category category = new Category("category");
        Seller seller = new Seller(new SellerId(1));
        BigDecimal price = BigDecimal.valueOf(50.00);
        long stock = 10L;
        Product product = new Product(1, "Product", category, seller, new Money(price), stock);

        int quantity = 3;
        product.consumeStock(quantity);

        assertEquals(stock-quantity, product.getStock());

    }

}
