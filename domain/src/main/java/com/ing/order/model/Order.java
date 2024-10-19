package com.ing.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Order {

    @Setter
    private String id;
    private final String customerId;
    private final String assetName;
    private final OrderSide side;
    private final BigDecimal size;
    private final BigDecimal price;
    @Setter
    private OrderStatus status;
    private final LocalDateTime createDate;

    public Order(String customerId, String assetName, OrderSide side, BigDecimal size, BigDecimal price) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.side = side;
        this.size = size;
        this.price = price;
        this.status = OrderStatus.PENDING;
        this.createDate = LocalDateTime.now();
    }

    public enum OrderSide {
        BUY, SELL
    }

    public enum OrderStatus {
        PENDING, MATCHED, CANCELED
    }
}