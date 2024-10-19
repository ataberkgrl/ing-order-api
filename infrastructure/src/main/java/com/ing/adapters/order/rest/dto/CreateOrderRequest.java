package com.ing.adapters.order.rest.dto;

import java.math.BigDecimal;

import com.ing.order.command.CreateOrderCommand;
import com.ing.order.model.Order;

public record CreateOrderRequest(
    String customerId,
    String assetName,
    Order.OrderSide side,
    BigDecimal size,
    BigDecimal price
) {

    public CreateOrderCommand toModel() {
        return CreateOrderCommand.builder()
            .customerId(customerId)
            .assetName(assetName)
            .side(side)
            .size(size)
            .price(price)
            .build();
    }

}