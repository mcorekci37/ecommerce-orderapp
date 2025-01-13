package com.emce.ecommerce.order.domain.entity;

import com.emce.ecommerce.common.domain.entity.AggregateRoot;
import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.product.domain.entity.Product;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

  private Integer userId;
  private Product product;
  private int quantity;
  private Money totalPrice;
  //    private Money price;
  //    private Money finalPrice;
  //    private Integer appliedPromotionId;
  private LocalDateTime date;
  private OrderStatus orderStatus;

  public Order(Product product, Integer quantity, Money totalPrice) {
    this.product = product;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.date = LocalDateTime.now();
    this.orderStatus = OrderStatus.CREATED;
    this.setId(new OrderId(UUID.randomUUID().toString()));
  }

  public Order(OrderId orderId, Integer userId, Product product, int quantity, Money totalPrice, LocalDateTime date, OrderStatus orderStatus) {
    setId(orderId);
    this.userId = userId;
    this.product = product;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.date = date;
    this.orderStatus = orderStatus;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
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

  public LocalDateTime getDate() {
    return date;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }
}
