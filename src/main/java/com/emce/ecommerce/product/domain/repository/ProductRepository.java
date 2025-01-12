package com.emce.ecommerce.product.domain.repository;

import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findByProductId(ProductId productId);
    Product save(Product product);
}
