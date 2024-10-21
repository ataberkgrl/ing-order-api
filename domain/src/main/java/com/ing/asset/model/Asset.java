package com.ing.asset.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Asset {

    private final String customerId;
    private final String assetName;
    @Setter
    private BigDecimal size;
    @Setter
    private BigDecimal usableSize;

    public void incrementSize(BigDecimal amount) {
        this.size = this.size.add(amount);
        this.usableSize = this.usableSize.add(amount);
    }

    public void incrementUsableSize(BigDecimal amount) {
        this.usableSize = this.usableSize.add(amount);
    }

    public void decrementSize(BigDecimal amount) {
        if (this.usableSize.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient usable size");
        }

        this.size = this.size.subtract(amount);
        this.usableSize = this.usableSize.subtract(amount);
    }

    public void decrementUsableSize(BigDecimal amount) {
        if (this.usableSize.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient usable size");
        }

        this.usableSize = this.usableSize.subtract(amount);
    }
}