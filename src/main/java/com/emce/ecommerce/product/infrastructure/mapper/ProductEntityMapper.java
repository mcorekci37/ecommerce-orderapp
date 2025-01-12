package com.emce.ecommerce.product.infrastructure.mapper;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import com.emce.ecommerce.product.infrastructure.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEntityMapper {

  private final CategoryEntityMapper categoryEntityMapper;

  public Product productEntityToProduct(ProductEntity entity) {
    return new Product(
        entity.getId(),
        entity.getName(),
        categoryEntityMapper.categoryEntityToCategory(entity.getCategoryEntity()),
        new Seller(new SellerId(entity.getSellerId())),
        new Money(entity.getPrice()),
        entity.getStock());
  }

  public ProductEntity productToProductEntity(Product product) {
    return new ProductEntity(
        product.getId().getValue(),
        product.getName(),
        categoryEntityMapper.categoryToCategoryEntity(product.getCategory()),
        product.getSeller().getId().getValue(),
        product.getPrice().amount(),
        product.getStock());
  }
}
