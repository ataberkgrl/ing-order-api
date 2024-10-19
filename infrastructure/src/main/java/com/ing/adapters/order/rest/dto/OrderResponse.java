package com.ing.adapters.order.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ing.order.model.Order;

public record OrderResponse(
    String id,
    String customerId,
    String assetName,
    Order.OrderSide side,
    BigDecimal size,
    BigDecimal price,
    Order.OrderStatus status,
    LocalDateTime createDate
) {

    public static OrderResponse fromModel(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getAssetName(),
            order.getSide(),
            order.getSize(),
            order.getPrice(),
            order.getStatus(),
            order.getCreateDate()
        );
    }

}