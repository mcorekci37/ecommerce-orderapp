package com.emce.ecommerce.product.domain.entity;

import com.emce.ecommerce.common.domain.entity.BaseEntity;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;

public class Seller extends BaseEntity<SellerId> {
    public Seller(SellerId sellerId) {
        setId(sellerId);
    }
    public Seller(Integer id) {
        this(new SellerId(id));
    }

}
