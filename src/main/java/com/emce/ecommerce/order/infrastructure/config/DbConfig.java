package com.emce.ecommerce.order.infrastructure.config;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import com.emce.ecommerce.product.domain.repository.CategoryRepository;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DbConfig implements CommandLineRunner {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public void run(String... args) throws Exception {

    Category renewedCategory = new Category(1, "renewed category");
    Category brandNewCategory = new Category(2, "brand new category");
    categoryRepository.save(renewedCategory);
    categoryRepository.save(brandNewCategory);

    Product product = new Product(10, "iphone 11", renewedCategory, getSeller(1), Money.of(15000.0), 100L);
    productRepository.save(product);
    product = new Product(20, "iphone 16 pro max", brandNewCategory, getSeller(1), Money.of(100000.0), 100L);
    productRepository.save(product);


  }

  private static Seller getSeller(int id) {
    return new Seller(new SellerId(id));
  }
}
