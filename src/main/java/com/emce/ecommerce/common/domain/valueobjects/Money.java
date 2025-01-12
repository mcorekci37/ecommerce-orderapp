package com.emce.ecommerce.common.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public static Money of(double amount) {
        return new Money(new BigDecimal(amount));
    }

    public boolean isGreaterThan(Money money) {
        return this.amount != null && this.amount.compareTo(money.amount()) > 0;
    }

    public boolean isGreaterThanOrEqual(Money money) {
        return this.amount != null && this.amount.compareTo(money.amount()) >= 0;
    }

    public Money add(Money money) {
        return new Money(setScale(this.amount.add(money.getAmount())));
    }

    public Money subtract(Money money) {
        return new Money(setScale(this.amount.subtract(money.getAmount())));
    }

    public Money multiply(int multiplier) {
        return new Money(setScale(this.amount.multiply(new BigDecimal(multiplier))));
    }

    public Money multiply(double multiplier) {
        return new Money(setScale(this.amount.multiply(new BigDecimal(multiplier))));
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
    public BigDecimal getAmount() {
        return amount;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
