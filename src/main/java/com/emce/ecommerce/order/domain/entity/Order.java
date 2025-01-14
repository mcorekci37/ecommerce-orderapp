package com.emce.ecommerce.order.domain.entity;

import com.emce.ecommerce.common.domain.entity.AggregateRoot;
import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.exception.ShippedOrderCannotBeCancelledException;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.product.domain.entity.Product;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

  private String username;
  private Product product;
  private int quantity;
  private Money totalPrice;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private OrderStatus orderStatus;

  public Order(String username, Product product, Integer quantity, Money totalPrice) {
    this.username = username;
    this.product = product;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.orderStatus = OrderStatus.CREATED;
    this.setId(new OrderId(UUID.randomUUID().toString()));
    this.product.consumeStock(quantity);
  }

  public Order(OrderId orderId, String username, Product product, int quantity, Money totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt, OrderStatus orderStatus) {
    setId(orderId);
    this.username = username;
    this.product = product;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.orderStatus = orderStatus;
  }

  public String getUsername() {
    return username;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public Money getTotalPrice() {
    return totalPrice;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void cancel() {
    if (shipped()){
      throw new ShippedOrderCannotBeCancelledException(getId().getValue());
    }
    this.orderStatus = OrderStatus.CANCELED;
    this.getProduct().undoStock(quantity);
  }

  private boolean shipped() {
    return getOrderStatus().equals(OrderStatus.SHIPPED);
  }
}
