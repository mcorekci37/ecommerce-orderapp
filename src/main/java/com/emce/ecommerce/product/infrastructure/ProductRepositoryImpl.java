package com.emce.ecommerce.product.infrastructure;

import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.infrastructure.mapper.ProductEntityMapper;
import com.emce.ecommerce.product.infrastructure.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    @Override
    public Optional<Product> findByProductId(ProductId productId) {
        return jpaRepository.findById(productId.getValue()).map(mapper::productEntityToProduct);
    }
    @Override
    public Product save(Product item) {
        return mapper.productEntityToProduct(jpaRepository
                .save(mapper.productToProductEntity(item)));
    }
}
