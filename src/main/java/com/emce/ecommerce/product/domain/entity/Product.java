package com.emce.ecommerce.product.domain.entity;

import com.emce.ecommerce.common.domain.entity.BaseEntity;
import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.exception.OutOfStockException;

public class Product extends BaseEntity<ProductId> {
  private String name;
  private Category category;
  private Seller seller;
  private Money price;
  private Long stock;

  public Product(String name, Category category, Seller seller, Money price) {
    this.name = name;
    this.category = category;
    this.seller = seller;
    this.price = price;
  }

  public Product(String name, Category category, Seller seller, Money price, Long stock) {
    this(name, category, seller, price);
    this.stock = stock;
  }

  public Product(
      Integer id, String name, Category category, Seller seller, Money price, Long stock) {
    this(name, category, seller, price, stock);
    this.setId(new ProductId(id));
  }

  public Category getCategory() {
    return category;
  }

  public Seller getSeller() {
    return seller;
  }

  public String getName() {
    return name;
  }

  public Money getPrice() {
    return price;
  }

  public Long getStock() {
    return stock;
  }

  public void consumeStock(long quantity) {
    if (stock > quantity) {
      stock -= quantity;
    } else {
      throw new OutOfStockException(quantity);
    }
  }

  public void undoStock(long quantity) {
    stock += quantity;
  }
}
