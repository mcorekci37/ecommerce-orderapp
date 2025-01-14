package com.emce.ecommerce.security.user.domain.valueobjects;

import com.emce.ecommerce.common.domain.valueobjects.BaseId;

public class CustomerId extends BaseId<Integer> {
    public CustomerId(Integer value) {
        super(value);
    }
}
