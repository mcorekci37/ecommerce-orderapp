package com.emce.ecommerce.product.infrastructure;

import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.infrastructure.entity.ProductEntity;
import com.emce.ecommerce.product.infrastructure.mapper.ProductEntityMapper;
import com.emce.ecommerce.product.infrastructure.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductRepositoryImplTest {

    @Mock
    private ProductEntity mockProductEntity;
    @Mock
    private ProductJpaRepository jpaRepository;
    @Mock
    private ProductEntityMapper mapper;
    @InjectMocks
    private ProductRepositoryImpl productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldSucceed() {
        Product product = mock(Product.class);
        when(mapper.productToProductEntity(product)).thenReturn(mockProductEntity);
        when(jpaRepository.save(mockProductEntity)).thenReturn(mockProductEntity);
        when(mapper.productEntityToProduct(mockProductEntity)).thenReturn(product);

        Product savedProduct = productRepository.save(product);

        assertEquals(product, savedProduct);
        verify(mapper).productToProductEntity(product);
        verify(jpaRepository).save(mockProductEntity);
        verify(mapper).productEntityToProduct(mockProductEntity);
    }

    @Test
    void findByOrderId_shouldSucceed() {
        ProductId productId = new ProductId(1);
        Product mockProduct = mock(Product.class);

        when(jpaRepository.findById(productId.getValue())).thenReturn(Optional.of(mockProductEntity));
        when(mapper.productEntityToProduct(mockProductEntity)).thenReturn(mockProduct);

        Optional<Product> foundProduct = productRepository.findByProductId(productId);

        assertTrue(foundProduct.isPresent());
        assertEquals(mockProduct, foundProduct.get());
        verify(jpaRepository).findById(productId.getValue());
    }

}
