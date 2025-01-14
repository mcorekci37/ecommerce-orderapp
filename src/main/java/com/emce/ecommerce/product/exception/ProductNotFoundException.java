package com.emce.ecommerce.product.exception;

public class ProductNotFoundException extends ProductDomainException {
    private final int productId;
    public ProductNotFoundException(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }
}
