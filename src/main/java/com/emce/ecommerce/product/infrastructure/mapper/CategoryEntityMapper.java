package com.emce.ecommerce.product.infrastructure.mapper;

import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.infrastructure.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {
    public Category categoryEntityToCategory(CategoryEntity categoryEntity) {
        return new Category(categoryEntity.getId(), categoryEntity.getName());
    }
    public CategoryEntity categoryToCategoryEntity(Category category) {
        return new CategoryEntity(
                category.getId()!=null ? category.getId().getValue() : null,
                category.getName());
    }

}
