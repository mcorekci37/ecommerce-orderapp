package com.emce.ecommerce.product.domain.valueobjects;

import com.emce.ecommerce.common.domain.valueobjects.BaseId;

public class ProductId extends BaseId<Integer> {
    public ProductId(Integer value) {
        super(value);
    }
}
