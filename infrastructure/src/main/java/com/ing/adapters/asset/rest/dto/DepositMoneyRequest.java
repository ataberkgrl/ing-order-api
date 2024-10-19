package com.ing.adapters.asset.rest.dto;

import java.math.BigDecimal;

import com.ing.asset.command.DepositMoneyCommand;

public record DepositMoneyRequest(
    String customerId,
    BigDecimal amount
) {

    public DepositMoneyCommand toModel() {
        return DepositMoneyCommand.builder()
            .customerId(customerId)
            .amount(amount)
            .build();
    }

}