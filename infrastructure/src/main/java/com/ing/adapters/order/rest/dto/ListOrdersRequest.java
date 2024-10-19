package com.ing.adapters.order.rest.dto;

import java.time.LocalDateTime;

import com.ing.order.command.ListOrdersCommand;

public record ListOrdersRequest(
    String customerId,
    LocalDateTime startDate,
    LocalDateTime endDate
) {

    public ListOrdersCommand toModel() {
        return ListOrdersCommand.builder()
            .customerId(customerId)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

}