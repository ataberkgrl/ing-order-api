package com.ing.adapters.order.rest.dto;

import com.ing.order.command.CancelOrderCommand;

public record CancelOrderRequest(
    String orderId,
    String customerId
) {

    public CancelOrderCommand toModel() {
        return CancelOrderCommand.builder()
            .orderId(orderId)
            .customerId(customerId)
            .build();
    }

}