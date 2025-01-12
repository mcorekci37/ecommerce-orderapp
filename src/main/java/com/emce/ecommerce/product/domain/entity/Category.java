package com.emce.ecommerce.product.domain.entity;

import com.emce.ecommerce.common.domain.entity.BaseEntity;
import com.emce.ecommerce.product.domain.valueobjects.CategoryId;

public class Category extends BaseEntity<CategoryId> {
  private String name;

  public Category(String name) {
    this.name = name;
  }

  public Category(Integer id, String name) {
    this(name);
    setId(new CategoryId(id));
  }

  public Category(Integer id) {
    setId(new CategoryId(id));
  }

  public String getName() {
    return name;
  }
}
