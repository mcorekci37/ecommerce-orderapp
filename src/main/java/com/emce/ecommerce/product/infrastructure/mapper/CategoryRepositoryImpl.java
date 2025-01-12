package com.emce.ecommerce.product.infrastructure.mapper;

import java.util.Optional;

import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.repository.CategoryRepository;
import com.emce.ecommerce.product.domain.valueobjects.CategoryId;
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

    @Override
    public Optional<Category> findByCategoryId(CategoryId categoryId) {
        return jpaRepository.findById(categoryId.getValue()).map(mapper::categoryEntityToCategory);
    }
}
