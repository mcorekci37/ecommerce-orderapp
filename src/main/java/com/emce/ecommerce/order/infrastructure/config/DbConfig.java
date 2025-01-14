package com.emce.ecommerce.order.infrastructure.config;

import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import com.emce.ecommerce.product.domain.repository.CategoryRepository;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import com.emce.ecommerce.security.user.domain.entity.Customer;
import com.emce.ecommerce.security.user.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.user.domain.valueobjects.CustomerId;
import com.emce.ecommerce.security.user.domain.valueobjects.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DbConfig implements CommandLineRunner {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;

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

    LocalDateTime now = LocalDateTime.now();
    try{
      Customer customer =
              new Customer(
                      new CustomerId(999), "admin", "admin@ecommerce.com", passwordEncoder.encode("adminpw"), now, now, Role.ADMIN);
      customerRepository.save(customer);
    }catch (Exception e){
      log.info(e.getMessage());
    }
  }

  private static Seller getSeller(int id) {
    return new Seller(new SellerId(id));
  }
}
