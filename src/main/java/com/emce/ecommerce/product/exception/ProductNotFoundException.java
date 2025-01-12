package com.emce.ecommerce.product.exception;

public class ProductNotFoundException extends ProductDomainException {
    public ProductNotFoundException(String message) {
        super(message);
    }
    public ProductNotFoundException(int productId) {
        this(String.format("Product with id %s not found", productId));
    }

}
