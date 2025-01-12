package com.emce.ecommerce.product.domain.repository;

import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.valueobjects.CategoryId;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category cart);
    Optional<Category> findByCategoryId(CategoryId categoryId);
}
