package com.emce.ecommerce.order.domain.valueobjects;

import com.emce.ecommerce.common.domain.valueobjects.BaseId;

public class OrderId extends BaseId<String> {
    public OrderId(String value) {
        super(value);
    }
}
