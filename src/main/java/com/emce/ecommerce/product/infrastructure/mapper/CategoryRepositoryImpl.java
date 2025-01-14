package com.emce.ecommerce.product.infrastructure.mapper;

import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.repository.CategoryRepository;
import com.emce.ecommerce.product.infrastructure.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryEntityMapper mapper;

    @Override
    public Category save(Category category) {
        return mapper.categoryEntityToCategory(jpaRepository
                .save(mapper.categoryToCategoryEntity(category)));
    }
}
