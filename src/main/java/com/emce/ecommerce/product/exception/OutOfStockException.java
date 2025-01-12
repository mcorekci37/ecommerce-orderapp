package com.emce.ecommerce.product.exception;

public class OutOfStockException extends ProductDomainException {
    public OutOfStockException(String message) {
        super(message);
    }
    public OutOfStockException(long quantity) {
        super(String.format("There is no enough stock for %s quantity", quantity));
    }

}
